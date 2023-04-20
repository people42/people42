package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.feed.PlaceFeedResDto;
import com.fourtytwo.dto.feed.RecentFeedResDto;
import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.Place;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.place.PlaceRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FeedService {

    private final BrushRepository brushRepository;
    private final MessageRepository messageRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

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
                MessageResDto messageResDto = MessageResDto
                        .builder()
                        .messageIdx(message.getId())
                        .content(message.getContent())
                        .userIdx(message.getUser().getId())
                        .nickname(message.getUser().getNickname())
                        .emoji(message.getUser().getEmoji())
                        .color(message.getUser().getColor())
                        .brushCnt(count)
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

    public PlaceFeedResDto findPlaceFeeds(String accessToken, Long placeIdx, LocalDateTime time) {
        Long userIdx = checkUserByAccessToken(accessToken);

        List<MessageResDto> messageResDtos = new ArrayList<>();
        // 24시간 이내 스침 조회
        List<Brush> brushList = brushRepository.findRecentBrushByUserIdxOrderByTimeDesc(userIdx);
        boolean flag = false;
        for (Brush brush : brushList) {
            if (brush.getPlace().getId().equals(placeIdx) && brush.getCreatedAt().equals(time)) {
                flag = true;
            }

            if (flag) {
                if (!brush.getPlace().getId().equals(placeIdx)) {
                    break;
                }

                // 해당 스침에서 메시지 조회
                Message message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
                if (message == null) {
                    continue;
                }

                // 상대 유저와 몇 번 스쳤는지 조회
                Long count = brushRepository.findBrushCntByUserIdxs(brush.getUser1().getId(), brush.getUser2().getId());
                MessageResDto messageResDto = MessageResDto.builder()
                        .messageIdx(message.getId())
                        .content(message.getContent())
                        .userIdx(message.getUser().getId())
                        .nickname(message.getUser().getNickname())
                        .emoji(message.getUser().getNickname())
                        .color(message.getUser().getColor())
                        .brushCnt(count)
                        .build();
                messageResDtos.add(messageResDto);
            }
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
