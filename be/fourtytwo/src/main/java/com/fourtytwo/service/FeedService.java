package com.fourtytwo.service;

import com.fourtytwo.dto.feed.RecentFeedResDto;
import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.Place;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FeedService {

    private final BrushRepository brushRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<RecentFeedResDto> findRecentBrush(Long userIdx) {
        if (userRepository.findById(userIdx).isEmpty()) {
            System.out.println("유저가 존재하지 않습니다.");
            return null;
        }
        List<Brush> recentBrushList = brushRepository.findRecentBrushByUserIdxOrderByTimeDesc(userIdx);
        List<RecentFeedResDto> recentFeedResDtos = new ArrayList<>();
        Place currentPlace = Place.builder().id(-1L).build();
        for (Brush brush : recentBrushList) {
            if (!currentPlace.getId().equals(brush.getPlace().getId())) {
                currentPlace = brush.getPlace();

                PlaceWithTimeResDto placeWithTimeResDto = PlaceWithTimeResDto
                        .builder()
                        .placeIdx(currentPlace.getId())
                        .placeName(currentPlace.getName())
                        .time(brush.getCreatedAt())
                        .build();

                Message message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
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

                RecentFeedResDto recentFeedResDto = new RecentFeedResDto();
                recentFeedResDto.setRecentMessageInfo(messageResDto);
                recentFeedResDto.setPlaceWithTimeInfo(placeWithTimeResDto);
                recentFeedResDtos.add(recentFeedResDto);
            }
        }

        return recentFeedResDtos;
    }

}
