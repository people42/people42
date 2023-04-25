package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.brush.BrushResDto;
import com.fourtytwo.dto.feed.PlaceFeedResDto;
import com.fourtytwo.dto.feed.RecentFeedResDto;
import com.fourtytwo.dto.feed.UserFeedResDto;
import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.place.PlaceResDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.entity.*;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.place.PlaceRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
public class FeedService {

    private final BrushRepository brushRepository;
    private final MessageRepository messageRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExpressionRepository expressionRepository;

    public List<RecentFeedResDto> findRecentBrush(String accessToken) {
        Long userIdx = checkUserByAccessToken(accessToken);

        // 24시간 이내 스침 조회
        List<Brush> recentBrushList = brushRepository.findRecentBrushByUserIdxOrderByTimeDesc(userIdx);
        List<RecentFeedResDto> recentFeedResDtos = new ArrayList<>();
        Place currentPlace = Place.builder().id(-1L).build();
        for (Brush brush : recentBrushList) {
            // 새로운 장소인 경우
            if (!currentPlace.getId().equals(brush.getPlace().getId())) {
                currentPlace = brush.getPlace();

                Message message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
                // 해당 장소에서 메시지가 없다면 넘기기
                if (message == null) {
                    currentPlace = Place.builder().id(-1L).build();
                    continue;
                }

                // 상대 유저와 몇 번 스쳤는지 조회
                Long count = brushRepository.findBrushCntByUserIdxs(brush.getUser1().getId(), brush.getUser2().getId());
                Optional<Expression> expression = expressionRepository.findByMessageAndUserId(message, userIdx);
                MessageResDto messageResDto = MessageResDto
                        .builder()
                        .messageIdx(message.getId())
                        .content(message.getContent())
                        .userIdx(message.getUser().getId())
                        .nickname(message.getUser().getNickname())
                        .emoji(message.getUser().getEmoji())
                        .color(message.getUser().getColor())
                        .brushCnt(count)
                        .emotion(expression.map(Expression::getEmotion).orElse(null))
                        .build();

                // 해당 장소와 시간 저장
                PlaceWithTimeResDto placeWithTimeResDto = PlaceWithTimeResDto
                        .builder()
                        .placeIdx(currentPlace.getId())
                        .placeName(currentPlace.getName())
                        .time(brush.getCreatedAt())
                        .build();

                // Dto 저장
                RecentFeedResDto recentFeedResDto = new RecentFeedResDto();
                recentFeedResDto.setRecentMessageInfo(messageResDto);
                recentFeedResDto.setPlaceWithTimeInfo(placeWithTimeResDto);
                recentFeedResDtos.add(recentFeedResDto);
            }
        }

        return recentFeedResDtos;
    }

    public PlaceFeedResDto findPlaceFeeds(String accessToken, Long placeIdx, LocalDateTime time, Integer page, Integer size) {
        Long userIdx = checkUserByAccessToken(accessToken);

        List<MessageResDto> messageResDtos = new ArrayList<>();
        // 24시간 이내 스침 조회
        List<Brush> brushList = brushRepository.findRecentBrushByUserIdxOrderByTimeDesc(userIdx);
        boolean flag = false;
        int cnt = 0;
        for (Brush brush : brushList) {
            // 요청받은 장소와 시간에 해당하는 스침을 조회하면 flag = true로 변경
            if (brush.getPlace().getId().equals(placeIdx)
                    && brush.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")).equals(time.toString())) {
                flag = true;
            }

            if (flag) {
                // 요청한 장소를 벗어나거나 요청받은 페이징 범위를 벗어나면 break
                if (!brush.getPlace().getId().equals(placeIdx) || cnt == (page + 1) * size) {
                    break;
                }

                // 해당 스침에서 메시지 조회
                Message message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
                if (message == null) {
                    continue;
                }

                cnt++;
                if (cnt > page * size) {
                    // 상대 유저와 몇 번 스쳤는지 조회
                    Long count = brushRepository.findBrushCntByUserIdxs(brush.getUser1().getId(), brush.getUser2().getId());
                    Optional<Expression> expression = expressionRepository.findByMessageAndUserId(message, userIdx);
                    MessageResDto messageResDto = MessageResDto.builder()
                            .messageIdx(message.getId())
                            .content(message.getContent())
                            .userIdx(message.getUser().getId())
                            .nickname(message.getUser().getNickname())
                            .emoji(message.getUser().getNickname())
                            .color(message.getUser().getColor())
                            .brushCnt(count)
                            .emotion(expression.map(Expression::getEmotion).orElse(null))
                            .build();
                    messageResDtos.add(messageResDto);
                }
            }
        }
        
        if (!flag) {
            throw new EntityNotFoundException("존재하지 않는 요청입니다.");
        }

        // 장소 조회
        Optional<Place> place = placeRepository.findById(placeIdx);
        PlaceWithTimeResDto placeWithTimeResDto;
        if (place.isPresent()) {
            placeWithTimeResDto = PlaceWithTimeResDto.builder()
                    .placeIdx(placeIdx)
                    .placeName(place.get().getName())
                    .time(time)
                    .build();
        } else {
            throw new EntityNotFoundException("존재하지 않는 장소입니다.");
        }

        return PlaceFeedResDto.builder()
                .messagesInfo(messageResDtos)
                .placeWithTimeInfo(placeWithTimeResDto)
                .build();
    }

    public UserFeedResDto findUserFeeds(String accessToken, Long targetUserIdx) {
        Long userIdx = checkUserByAccessToken(accessToken);
        List<PlaceResDto> placeResDtos = new ArrayList<>();
        HashMap<Place, Integer> placeMap = new HashMap<>();

        List<Brush> brushList = brushRepository.findBrushesByUser1IdAndUser2Id(userIdx, targetUserIdx);

        for (Brush brush : brushList) {
            if (placeMap.containsKey(brush.getPlace())) {
                placeMap.put(brush.getPlace(), placeMap.get(brush.getPlace())+1);
            } else {
                placeMap.put(brush.getPlace(), 1);
            }
        }
        placeMap.forEach((place, cnt) -> {
            PlaceResDto placeResDto = PlaceResDto.builder().placeIdx(place.getId()).placeName(place.getName())
                    .placeLongitude(place.getLongitude()).placeLatitude(place.getLatitude()).brushCnt(cnt).build();
            placeResDtos.add(placeResDto);
        });
        return UserFeedResDto.builder()
                .placeResDtos(placeResDtos)
                .brushCnt(brushList.size())
                .userIdx(targetUserIdx)
                .nickname(userRepository.findByIdAndIsActiveTrue(targetUserIdx).getNickname())
                .build();
    }

    // 액세스 토큰 확인 및 유저 인덱스 반환
    public Long checkUserByAccessToken(String accessToken) {
        Long userIdx = jwtTokenProvider.getUserIdx(accessToken);
        Optional<User> user = userRepository.findById(userIdx);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("유저가 존재하지 않습니다.");
        } else if (!user.get().getIsActive()) {
            throw new EntityNotFoundException("삭제된 유저입니다.");
        }
        return userIdx;
    }

}
