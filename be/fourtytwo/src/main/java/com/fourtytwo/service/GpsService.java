package com.fourtytwo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtytwo.dto.place.GpsReqDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Place;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.message.MessageRepositoryImpl;
import com.fourtytwo.repository.place.PlaceRepository;
import com.fourtytwo.repository.place.PlaceRepositoryImpl;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GpsService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final BrushRepository brushRepository;
    private final MessageRepositoryImpl messageRepositoryImpl;
    private final PlaceRepositoryImpl placeRepositoryImpl;
    private final RedisTemplate<String, Long> gpsTemplate;
    private final RedisTemplate<Long, Integer> userTimeTemplate;
    private final RedisTemplate<Integer, Long> timeUserTemplate;
    private final RedisTemplate<Integer, String> timeBrushTemplate;
    private final RedisTemplate<String, String> brushTemplate;
    private final String kakaoRestApiKey;

    public PlaceWithTimeResDto renewGps(String accessToken, GpsReqDto gps) {
        Long userIdx = userService.checkUserByAccessToken(accessToken);
        Place foundPlace = placeRepositoryImpl.findByGps(gps.getLatitude(), gps.getLongitude());

        LocalDateTime current = LocalDateTime.now();
        Integer mappedTime = toTotalMinutes(current);

        System.out.println("1 "+foundPlace);
        if (foundPlace == null) {
            List<Map<String, Object>> popularPlaces = getPopularPlaces(gps.getLatitude(), gps.getLongitude());
            Map<String, Object> targetPlace;
            if (popularPlaces.isEmpty()) {
                targetPlace = getRoadAddress(gps.getLatitude(), gps.getLongitude());
            } else {
                targetPlace = getPopularPlaces(gps.getLatitude(), gps.getLongitude()).get(0);
            }
            Place newPlace = new Place();
            newPlace.setName((String) targetPlace.get("place_name"));
            newPlace.setLatitude(Double.parseDouble((String) targetPlace.get("y")));
            newPlace.setLongitude(Double.parseDouble((String) targetPlace.get("x")));
            foundPlace = placeRepository.save(newPlace);
        }
        System.out.println("2 "+foundPlace);
        ZSetOperations<String, Long> gpsOperation = gpsTemplate.opsForZSet();
        SetOperations<Integer, Long> expireSetOperation = timeUserTemplate.opsForSet();
        ValueOperations<Long, Integer> userExpireOperation = userTimeTemplate.opsForValue();
        SetOperations<Integer, String> timeBrushOperation = timeBrushTemplate.opsForSet();
        SetOperations<String, String> brushOperation = brushTemplate.opsForSet();
        gpsOperation.add("latitude", userIdx, gps.getLatitude());
        gpsOperation.add("longitude", userIdx, gps.getLongitude());
        if (userExpireOperation.get(userIdx) == null) {

        } else {
            Integer prevTime = userExpireOperation.get(userIdx);
            expireSetOperation.remove(prevTime, userIdx);
        }
        userExpireOperation.set(userIdx, mappedTime + 10);
        expireSetOperation.add(mappedTime+10, userIdx);

        Set<Long> nearSet = gpsOperation.rangeByScore("latitude", gps.getLatitude()-0.005, gps.getLatitude()+0.005);
        Set<Long> nearLongSet = gpsOperation.rangeByScore("longitude", gps.getLongitude()-0.005, gps.getLongitude()+0.005);

        System.out.println("3 "+nearSet);
        System.out.println("4 "+nearLongSet);
        if (!nearSet.isEmpty() && !nearLongSet.isEmpty()) {
            nearSet.retainAll(nearLongSet);
            if (!nearSet.isEmpty()) {
                for (Long targetIdx : nearSet) {
                    if (userIdx < targetIdx) {
                        if (Boolean.TRUE.equals(brushOperation.isMember("brushes", userIdx.toString()+targetIdx.toString()+
                                foundPlace+messageRepositoryImpl.findRecentByUserIdx(userIdx)+messageRepositoryImpl.findRecentByUserIdx(targetIdx)))) {
                            continue;
                        }
                        Brush newBrush = Brush.builder()
                                .user1(userRepository.findByIdAndIsActiveTrue(userIdx))
                                .user2(userRepository.findByIdAndIsActiveTrue(targetIdx))
                                .message1(messageRepositoryImpl.findRecentByUserIdx(userIdx))
                                .message2(messageRepositoryImpl.findRecentByUserIdx(targetIdx))
                                .place(foundPlace)
                                .build();
                        brushRepository.save(newBrush);
                        brushOperation.add("brushes", userIdx.toString()+targetIdx.toString()+
                                foundPlace+messageRepositoryImpl.findRecentByUserIdx(userIdx)+messageRepositoryImpl.findRecentByUserIdx(targetIdx));
                        timeBrushOperation.add(mappedTime+180, userIdx.toString()+targetIdx.toString()+
                                foundPlace+messageRepositoryImpl.findRecentByUserIdx(userIdx)+messageRepositoryImpl.findRecentByUserIdx(targetIdx));
                    }
                }
            }
        }
        return PlaceWithTimeResDto.builder()
                .placeIdx(foundPlace.getId())
                .placeName(foundPlace.getName())
                .time(current)
                .build();


    }

    private List<Map<String, Object>> getPopularPlaces(double latitude, double longitude) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> categoryCodes = Arrays.asList("SC4", "SW8", "PO3", "AT4", "AD5", "CT1");
        List<Map<String, Object>> popularPlaces = new ArrayList<>();

        for (String categoryCode : categoryCodes) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/search/category.json")
                    .queryParam("category_group_code", categoryCode)
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("radius", 500)
                    .queryParam("sort", "popularity");

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
            };

            Map<String, Object> responseMap;
            try {
                responseMap = objectMapper.readValue(responseEntity.getBody(), typeRef);
            } catch (IOException e) {
                throw new RuntimeException("Error parsing response", e);
            }

            TypeReference<List<Map<String, Object>>> listTypeRef = new TypeReference<>() {
            };

            List<Map<String, Object>> places = objectMapper.convertValue(responseMap.get("documents"), listTypeRef);
            if (!places.isEmpty()) {
                popularPlaces.add(places.get(0));
                break;
            }
        }
        System.out.println(popularPlaces);

        return popularPlaces;
    }

    private Map<String, Object> getRoadAddress(double latitude, double longitude) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                .queryParam("x", longitude)
                .queryParam("y", latitude);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
        };

        Map<String, Object> responseMap;
        try {
            responseMap = objectMapper.readValue(responseEntity.getBody(), typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing response", e);
        }

        TypeReference<List<Map<String, Object>>> listTypeRef = new TypeReference<>() {
        };
        List<Map<String, Object>> documents = objectMapper.convertValue(responseMap.get("documents"), listTypeRef);

        if (!documents.isEmpty()) {
            return (Map<String, Object>) documents.get(0).get("road_address");
        } else {
            throw new RuntimeException("No road address found for given coordinates");
        }
    }

    public static int toTotalMinutes(LocalDateTime dateTime) {
        long days = ChronoUnit.DAYS.between(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), dateTime);
        long minutes = days * 24 * 60 + dateTime.getHour() * 60 + dateTime.getMinute();
        return (int) minutes;
    }


}
