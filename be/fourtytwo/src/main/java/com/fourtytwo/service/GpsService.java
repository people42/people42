package com.fourtytwo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtytwo.dto.place.GpsReqDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.entity.*;
import com.fourtytwo.repository.block.BlockRepository;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.place.PlaceRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@EnableScheduling
public class GpsService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final BrushRepository brushRepository;
    private final MessageRepository messageRepository;
    private final BlockRepository blockRepository;
    private final RedisTemplate<String, Long> gpsTemplate;
    private final RedisTemplate<Long, Integer> userTimeTemplate;
    private final RedisTemplate<String, Long> timeUserTemplate;
    private final RedisTemplate<String, String> timeBrushTemplate;
    private final RedisTemplate<String, String> brushTemplate;
    private final String kakaoRestApiKey;
    private final String googleMapKey;
    private final RedissonClient redissonClient;

    public PlaceWithTimeResDto lockRenewGps(String accessToken, GpsReqDto gps) {
        RLock lock = redissonClient.getLock("renewGpsKey");

        try {
            boolean isLocked = lock.tryLock(1, 1, TimeUnit.SECONDS);
            if (!isLocked) {
                // 락 획득에 실패했으므로 예외 처리
                throw new DataAccessException("너무 많은 요청을 보냈습니다.") {
                };
            }

            return renewGps(accessToken, gps);

        } catch (InterruptedException e) {
            // 쓰레드가 인터럽트 될 경우의 예외 처리
            throw new RuntimeException(e);
        } finally {
            // 락 해제
            lock.unlock();
        }
    };

    public PlaceWithTimeResDto renewGps(String accessToken, GpsReqDto gps) {
        Long userIdx = userService.checkUserByAccessToken(accessToken);
        Optional<User> user = userRepository.findById(userIdx);
        if (user.isEmpty() || !user.get().getIsActive()) {
            return null;
        }
        Place foundPlace = placeRepository.findByGps(gps.getLatitude(), gps.getLongitude());
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};

        LocalDateTime current = LocalDateTime.now();
        Integer mappedTime = toTotalMinutes(current);

        if (foundPlace == null) {
            List<Map<String, Object>> popularPlaces = getPopularPlaces(gps.getLatitude(), gps.getLongitude());
            Map<String, Object> targetPlace;
            if (popularPlaces.isEmpty()) {
                targetPlace = getRoadAddress(gps.getLatitude(), gps.getLongitude());
                Place newPlace = new Place();
                newPlace.setName((String) targetPlace.get("place_name"));
                newPlace.setLatitude(Double.parseDouble((String) targetPlace.get("y")));
                newPlace.setLongitude(Double.parseDouble((String) targetPlace.get("x")));
                foundPlace = placeRepository.save(newPlace);
            } else {
                targetPlace = popularPlaces.get(0);
                String placeName = (String) targetPlace.get("name");
                Double placeLat = (Double) objectMapper.convertValue(objectMapper.convertValue(targetPlace.get("geometry"), typeRef).get("location"), typeRef).get("lat");
                Double placeLng = (Double) objectMapper.convertValue(objectMapper.convertValue(targetPlace.get("geometry"), typeRef).get("location"), typeRef).get("lng");

                Place newPlace = new Place();
                newPlace.setName(placeName);
                newPlace.setLatitude(placeLat);
                newPlace.setLongitude(placeLng);
                foundPlace = placeRepository.save(newPlace);
            }
        }
        ZSetOperations<String, Long> gpsOperation = gpsTemplate.opsForZSet();
        SetOperations<String, Long> expireSetOperation = timeUserTemplate.opsForSet();
        ValueOperations<Long, Integer> userExpireOperation = userTimeTemplate.opsForValue();
        SetOperations<String, String> timeBrushOperation = timeBrushTemplate.opsForSet();
        SetOperations<String, String> brushOperation = brushTemplate.opsForSet();
        gpsOperation.add("latitude", userIdx, gps.getLatitude());
        gpsOperation.add("longitude", userIdx, gps.getLongitude());
        if (userExpireOperation.get(userIdx) == null) {

        } else {
            Integer prevTime = userExpireOperation.get(userIdx);
            expireSetOperation.remove("user"+prevTime, userIdx);
        }
        userExpireOperation.set(userIdx, mappedTime + 10);
        expireSetOperation.add("user"+(mappedTime+10), userIdx);

        Set<Long> nearSet = gpsOperation.rangeByScore("latitude", gps.getLatitude()-0.002, gps.getLatitude()+0.002);
        Set<Long> nearLongSet = gpsOperation.rangeByScore("longitude", gps.getLongitude()-0.002, gps.getLongitude()+0.002);

        Message userMessage = messageRepository.findRecentByUserIdx(userIdx);
        if (userMessage == null) {
            return PlaceWithTimeResDto.builder()
                    .placeIdx(foundPlace.getId())
                    .placeName(foundPlace.getName())
                    .time(current)
                    .build();
        }

        if (!nearSet.isEmpty() && !nearLongSet.isEmpty()) {
            nearSet.retainAll(nearLongSet);
            if (!nearSet.isEmpty()) {
                for (Long targetIdx : nearSet) {
                    if (userIdx < targetIdx) {

                        // 차단된 유저 건너뛰기
                        Optional<Block> block = blockRepository.findByUser1IdAndUser2Id(userIdx, targetIdx);
                        if (block.isPresent()) {
                            continue;
                        }

                        Optional<User> oppositeUser = userRepository.findById(targetIdx);
                        if (oppositeUser.isEmpty() || !oppositeUser.get().getIsActive()) {
                            continue;
                        }
                        Message oppositeUserMessage = messageRepository.findRecentByUserIdx(targetIdx);
                        if (oppositeUserMessage == null) {
                            continue;
                        }
                        if (Boolean.TRUE.equals(brushOperation.isMember("brushes", userIdx.toString()+" "+targetIdx.toString()+" "+
                                foundPlace.getName()+" "+userMessage.getContent()+" "+oppositeUserMessage.getContent()))) {
                            continue;
                        }
                        Brush newBrush = Brush.builder()
                                .user1(user.get())
                                .user2(oppositeUser.get())
                                .message1(userMessage)
                                .message2(oppositeUserMessage)
                                .place(foundPlace)
                                .build();
                        brushRepository.save(newBrush);
                        brushOperation.add("brushes", userIdx.toString()+" "+targetIdx.toString()+" "+
                                foundPlace.getName()+" "+userMessage.getContent()+" "+oppositeUserMessage.getContent());
                        timeBrushOperation.add("brush"+(mappedTime+180), userIdx.toString()+" "+targetIdx.toString()+" "+
                                foundPlace.getName()+" "+userMessage.getContent()+" "+oppositeUserMessage.getContent());
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

    @Scheduled(fixedRate = 20000)
    public void deleteExpired() {
        LocalDateTime current = LocalDateTime.now();
        Integer mappedTime = toTotalMinutes(current);
        ZSetOperations<String, Long> gpsOperation = gpsTemplate.opsForZSet();
        SetOperations<String, Long> expireSetOperation = timeUserTemplate.opsForSet();
        ValueOperations<Long, Integer> userExpireOperation = userTimeTemplate.opsForValue();
        SetOperations<String, String> timeBrushOperation = timeBrushTemplate.opsForSet();
        SetOperations<String, String> brushOperation = brushTemplate.opsForSet();

        Set<Long> expiredUsers = expireSetOperation.members("user"+mappedTime);
        Set<String> expiredBrushes = timeBrushOperation.members("brush"+mappedTime);
        if (expiredUsers != null && !expiredUsers.isEmpty()) {
            for (Long userIdx : expiredUsers) {
                gpsOperation.remove("latitude", userIdx);
                gpsOperation.remove("longitude", userIdx);
                userExpireOperation.getAndDelete(userIdx);
            }
        }
        if (expiredBrushes != null && !expiredBrushes.isEmpty()) {
            for (String brush : expiredBrushes) {
                brushOperation.remove("brushes", brush);
            }
        }
        timeUserTemplate.delete("user"+mappedTime);
        timeBrushTemplate.delete("brush"+mappedTime);
    }


    private List<Map<String, Object>> getPopularPlaces(double latitude, double longitude) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryParam("location", latitude + "%2C" + longitude)
//                .queryParam("radius", 200)
                .queryParam("rankby", "distance")
                .queryParam("language", "ko")
                .queryParam("type", "point_of_interest")
                .queryParam("key", googleMapKey);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.build(true).toUri(),
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
        TypeReference<List<Map<String, Object>>> listTypeRef = new TypeReference<>() {};

        return objectMapper.convertValue(responseMap.get("results"), listTypeRef);
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
