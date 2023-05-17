package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.brush.BrushResDto;
import com.fourtytwo.dto.feed.*;
import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.message.UserMessageResDto;
import com.fourtytwo.dto.place.PlaceResDto;
import com.fourtytwo.dto.place.PlaceWithTimeAndGpsResDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.entity.*;
import com.fourtytwo.repository.block.BlockRepository;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.place.PlaceRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
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
    private final BlockRepository blockRepository;

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

//                User opponent;
//                if (brush.getUser1().getId().equals(userIdx)) {
//                    opponent = brush.getUser2();
//                } else {
//                    opponent = brush.getUser1();
//                }

                Message message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
                // 해당 장소에서 메시지가 없다면 넘기기
                if (message == null || !message.getIsActive()) {
                    currentPlace = Place.builder().id(-1L).build();
                    continue;
                }

                // 차단된 유저의 메시지라면 넘기기
                Long bigIdx = userIdx > message.getUser().getId() ? userIdx : message.getUser().getId();
                Long smallIdx = userIdx > message.getUser().getId() ? message.getUser().getId() : userIdx;
                Optional<Block> block = blockRepository.findByUser1IdAndUser2Id(smallIdx, bigIdx);
                if (block.isPresent()) {
                    currentPlace = Place.builder().id(-1L).build();
                    continue;
                }

                // 상대 유저와 몇 번 스쳤는지 조회
                // Long count = brushRepository.findBrushCntByUserIdxs(brush.getUser1().getId(), brush.getUser2().getId());

                List<Brush> brushes = brushRepository.findBrushesByUser1IdAndUser2IdAndUser1_IsActiveTrueAndUser2_IsActiveTrueAndMessage1_IsActiveTrueAndMessage2_IsActiveTrue(smallIdx, bigIdx);
                List<BrushInfo> brushMemo = new ArrayList<>();
                for (Brush tmpBrush : brushes) {
                    Long myMessageIdx = tmpBrush.getUser1().getId().equals(userIdx) ? tmpBrush.getMessage1().getId() : tmpBrush.getMessage2().getId();
                    Long oppositeMessageIdx = tmpBrush.getUser1().getId().equals(userIdx) ? tmpBrush.getMessage2().getId() : tmpBrush.getMessage1().getId();
                    BrushInfo currentBrushInfo = new BrushInfo(myMessageIdx, oppositeMessageIdx, tmpBrush.getCreatedAt());
                    boolean flag = false;
                    for (BrushInfo brushInfo : brushMemo) {
                        if (brushInfo.oppositeMessageIdx.equals(currentBrushInfo.getOppositeMessageIdx()) &&
                                brushInfo.getCreatedAt().minusHours(2L).isBefore(currentBrushInfo.getCreatedAt())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        continue;
                    }
                    brushMemo.add(currentBrushInfo);
                }

                Optional<Expression> expression = expressionRepository.findByMessageAndUserId(message, userIdx);
                MessageResDto messageResDto = MessageResDto
                        .builder()
                        .messageIdx(message.getId())
                        .content(message.getContent())
                        .userIdx(message.getUser().getId())
                        .nickname(message.getUser().getNickname())
                        .emoji(message.getUser().getEmoji())
                        .color(message.getUser().getColor())
                        .isInappropriate(message.getIsInappropriate())
                        .brushCnt((long) brushMemo.size())
                        .emotion(expression.map(Expression::getEmotion).map(Emotion::getName).orElse(null))
                        .build();

                // 해당 장소와 시간 저장
                PlaceWithTimeResDto placeWithTimeResDto = PlaceWithTimeResDto
                        .builder()
                        .placeIdx(currentPlace.getId())
                        .placeName(currentPlace.getName())
                        .time(brush.getCreatedAt().withNano(0))
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
        Set<Long> messageSet = new HashSet<>();

        for (Brush brush : brushList) {
            // 요청받은 장소와 시간에 해당하는 스침을 조회하면 flag = true로 변경
            if (brush.getPlace().getId().equals(placeIdx)
                    && brush.getCreatedAt().withNano(0).equals(time)) {
                flag = true;
            }

            if (flag) {
                // 요청한 장소를 벗어나거나 요청받은 페이징 범위를 벗어나면 break
                if (!brush.getPlace().getId().equals(placeIdx) || cnt == (page + 1) * size) {
                    break;
                }

                // 해당 스침에서 메시지 조회
                Message message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
                if (message == null || !message.getIsActive()) {
                    continue;
                }

                // 차단된 유저의 메시지라면 넘기기
                Long bigIdx = userIdx > message.getUser().getId() ? userIdx : message.getUser().getId();
                Long smallIdx = userIdx > message.getUser().getId() ? message.getUser().getId() : userIdx;
                Optional<Block> block = blockRepository.findByUser1IdAndUser2Id(smallIdx, bigIdx);
                if (block.isPresent()) {
                    continue;
                }

                // 이미 조회한 메시지라면 넘기기
                if (messageSet.contains(message.getId())) {
                    continue;
                }
                messageSet.add(message.getId());


                cnt++;
                if (cnt > page * size) {
                    // 상대 유저와 몇 번 스쳤는지 조회
                    // Long count = brushRepository.findBrushCntByUserIdxs(brush.getUser1().getId(), brush.getUser2().getId());

                    List<Brush> brushes = brushRepository.findBrushesByUser1IdAndUser2IdAndUser1_IsActiveTrueAndUser2_IsActiveTrueAndMessage1_IsActiveTrueAndMessage2_IsActiveTrue(smallIdx, bigIdx);
                    List<BrushWithPlaceInfo> brushMemo = new ArrayList<>();
                    for (Brush tmpBrush : brushes) {
                        Long myMessageIdx = tmpBrush.getUser1().getId().equals(userIdx) ? tmpBrush.getMessage1().getId() : tmpBrush.getMessage2().getId();
                        Long oppositeMessageIdx = tmpBrush.getUser1().getId().equals(userIdx) ? tmpBrush.getMessage2().getId() : tmpBrush.getMessage1().getId();
                        BrushWithPlaceInfo currentBrushInfo = new BrushWithPlaceInfo(myMessageIdx, oppositeMessageIdx, tmpBrush.getPlace().getId(), tmpBrush.getCreatedAt());
                        boolean cntFlag = false;
                        for (BrushWithPlaceInfo brushInfo : brushMemo) {
                            if (brushInfo.getPlaceIdx().equals(brush.getPlace().getId()) &&
                                    brushInfo.oppositeMessageIdx.equals(currentBrushInfo.getOppositeMessageIdx()) &&
                                    brushInfo.getCreatedAt().minusHours(2L).isBefore(currentBrushInfo.getCreatedAt())) {
                                cntFlag = true;
                                break;
                            }
                        }
                        if (cntFlag) {
                            continue;
                        }
                        brushMemo.add(currentBrushInfo);
                    }

                    Optional<Expression> expression = expressionRepository.findByMessageAndUserId(message, userIdx);
                    MessageResDto messageResDto = MessageResDto.builder()
                            .messageIdx(message.getId())
                            .content(message.getContent())
                            .userIdx(message.getUser().getId())
                            .nickname(message.getUser().getNickname())
                            .emoji(message.getUser().getEmoji())
                            .color(message.getUser().getColor())
                            .isInappropriate(message.getIsInappropriate())
                            .brushCnt((long) brushMemo.size())
                            .emotion(expression.map(Expression::getEmotion).map(Emotion::getName).orElse(null))
                            .build();
                    messageResDtos.add(messageResDto);
                }
            }
        }
        
        if (!flag) {
            throw new EntityNotFoundException("해당 유저가 해당 장소나 시간에서 스친 데이터가 없습니다.");
        }

        // 장소 조회
        Optional<Place> place = placeRepository.findById(placeIdx);
        PlaceWithTimeAndGpsResDto placeWithTimeAndGpsResDto;
        if (place.isPresent()) {
            placeWithTimeAndGpsResDto = PlaceWithTimeAndGpsResDto.builder()
                    .placeIdx(placeIdx)
                    .placeName(place.get().getName())
                    .time(time)
                    .placeLatitude(place.get().getLatitude())
                    .placeLongitude(place.get().getLongitude())
                    .build();
        } else {
            throw new EntityNotFoundException("존재하지 않는 장소입니다.");
        }

        return PlaceFeedResDto.builder()
                .messagesInfo(messageResDtos)
                .placeWithTimeAndGpsInfo(placeWithTimeAndGpsResDto)
                .build();
    }

    public UserFeedResDto findUserFeeds(String accessToken, Long targetUserIdx) {
        Long userIdx = checkUserByAccessToken(accessToken);
        User tartgetUser = userRepository.findByIdAndIsActiveTrue(targetUserIdx);
        if (tartgetUser == null) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        List<PlaceResDto> placeResDtos = new ArrayList<>();
        HashMap<Place, Integer> placeMap = new HashMap<>();

        Long bigIdx = userIdx > targetUserIdx ? userIdx : targetUserIdx;
        Long smallIdx = userIdx > targetUserIdx ? targetUserIdx : userIdx;

        List<Brush> brushList = brushRepository.findBrushesByUser1IdAndUser2IdAndUser1_IsActiveTrueAndUser2_IsActiveTrueAndMessage1_IsActiveTrueAndMessage2_IsActiveTrue(smallIdx, bigIdx);
        List<BrushWithPlaceInfo> brushMemo = new ArrayList<>();
        for (Brush brush : brushList) {
            Long myMessageIdx = brush.getUser1().getId().equals(userIdx) ? brush.getMessage1().getId() : brush.getMessage2().getId();
            Long oppositeMessageIdx = brush.getUser1().getId().equals(userIdx) ? brush.getMessage2().getId() : brush.getMessage1().getId();
            BrushWithPlaceInfo currentBrushWithPlaceInfo = new BrushWithPlaceInfo(myMessageIdx, oppositeMessageIdx, brush.getPlace().getId(), brush.getCreatedAt());
            boolean flag = false;
            for (BrushWithPlaceInfo brushInfo : brushMemo) {
                if (brushInfo.getPlaceIdx().equals(brush.getPlace().getId()) &&
                        brushInfo.oppositeMessageIdx.equals(currentBrushWithPlaceInfo.getOppositeMessageIdx()) &&
                        brushInfo.getCreatedAt().minusHours(2L).isBefore(currentBrushWithPlaceInfo.getCreatedAt())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }
            brushMemo.add(currentBrushWithPlaceInfo);
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
                .brushCnt(brushMemo.size())
                .userIdx(targetUserIdx)
                .nickname(tartgetUser.getNickname())
                .emoji(tartgetUser.getEmoji())
                .build();
    }

    public UserPlaceFeedResDto findUserPlaceFeeds(String accessToken, Long targetIdx, Long placeIdx) {

        Long userIdx = checkUserByAccessToken(accessToken);
        List<UserMessageResDto> userMessageResDtos = new ArrayList<>();
        List<BrushInfo> brushMemo = new ArrayList<>();

        Long bigIdx = userIdx > targetIdx ? userIdx : targetIdx;
        Long smallIdx = userIdx > targetIdx ? targetIdx : userIdx;

        List<Brush> brushes = brushRepository.findBrushesByUser1IdAndUser2IdAndPlaceIdAndMessage1_IsActiveTrueAndMessage2_IsActiveTrueOrderByCreatedAtDesc(smallIdx, bigIdx, placeIdx);
        for (Brush brush : brushes) {

            UserMessageResDto userMessageResDto = new UserMessageResDto();
            Message myMessage;
            Message oppositeMessage;
            if (userIdx < targetIdx) {
                myMessage = brush.getMessage1();
                oppositeMessage = brush.getMessage2();
            } else {
                myMessage = brush.getMessage2();
                oppositeMessage = brush.getMessage1();
            }

            BrushInfo currentBrushInfo = new BrushInfo(myMessage.getId(), oppositeMessage.getId(), brush.getCreatedAt());
            boolean flag = false;
            for (BrushInfo brushInfo : brushMemo) {
                if (brushInfo.oppositeMessageIdx.equals(currentBrushInfo.getOppositeMessageIdx()) &&
                        brushInfo.getCreatedAt().minusHours(2L).isBefore(currentBrushInfo.getCreatedAt())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }
            brushMemo.add(currentBrushInfo);

            userMessageResDto.setMessageIdx(oppositeMessage.getId());
            userMessageResDto.setContent(oppositeMessage.getContent());
            userMessageResDto.setTime(brush.getCreatedAt().withNano(0));
            userMessageResDto.setIsInappropriate(oppositeMessage.getIsInappropriate());
            Optional<Expression> expression = expressionRepository.findByMessageAndUserId(oppositeMessage, userIdx);
            userMessageResDto.setEmotion(expression.map(Expression::getEmotion).map(Emotion::getName).orElse(null));
            userMessageResDtos.add(userMessageResDto);
        }

        for (UserMessageResDto userMessageResDto : userMessageResDtos) {
            System.out.println("===start===");
            System.out.println(userMessageResDto.getContent());
            System.out.println("===end===");
        }

        return UserPlaceFeedResDto.builder()
                .messagesInfo(userMessageResDtos)
                .brushCnt(userMessageResDtos.size())
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

    public List<NewFeedResDto> findNewFeeds(String accessToken) {
        Long userIdx = checkUserByAccessToken(accessToken);

        // 24시간 이내 스침 조회
        List<Brush> recentBrushList = brushRepository.findRecentBrushByUserIdxOrderByTimeDesc(userIdx);
        recentBrushList.add(Brush.builder().id(-1L).build());
        List<NewFeedResDto> newFeedResDtos = new ArrayList<>();
        Place currentPlace = Place.builder().id(-1L).build();
        List<String> firstTimeUserEmojis = new ArrayList<>();
        List<String> repeatUserEmojis = new ArrayList<>();
        Set<Long> userIdxSet = new HashSet<>();
        Brush firstBrush = null;

        for (Brush brush : recentBrushList) {
            Message message = null;
            Long bigIdx = null;
            Long smallIdx = null;

            if (!brush.getId().equals(-1L)) {

                // 요청한 유저가 스쳤을 때 상대 메시지 조회
                message = messageRepository.findByBrushAndUserIdx(brush, userIdx);
                // 메시지가 없다면 넘기기
                if (message == null || !message.getIsActive()) {
                    continue;
                }

                // 차단된 유저의 메시지라면 넘기기
                bigIdx = userIdx > message.getUser().getId() ? userIdx : message.getUser().getId();
                smallIdx = userIdx > message.getUser().getId() ? message.getUser().getId() : userIdx;
                Optional<Block> block = blockRepository.findByUser1IdAndUser2Id(smallIdx, bigIdx);
                if (block.isPresent()) {
                    continue;
                }

            }

            // 새로운 장소인 경우
            if (brush.getId().equals(-1L) || !currentPlace.getId().equals(brush.getPlace().getId())) {

                if (firstBrush != null) {
                    // 상대 유저 조회
                    Long oppositeUserIdx = firstBrush.getUser1().getId().equals(userIdx) ? firstBrush.getUser2().getId() : firstBrush.getUser1().getId();
                    Optional<User> oppositeUser = userRepository.findById(oppositeUserIdx);
                    if (oppositeUser.isEmpty() || !oppositeUser.get().getIsActive()) {
                        currentPlace = Place.builder().id(-1L).build();
                        continue;
                    }

                    // Dto 저장
                    NewFeedResDto newFeedResDto = new NewFeedResDto();
                    newFeedResDto.setRecentUsersInfo(RecentUsersInfoResDto.builder()
                            .firstTimeUserEmojis(firstTimeUserEmojis)
                            .repeatUserEmojis(repeatUserEmojis)
                            .nickname(oppositeUser.get().getNickname())
                            .userCnt(firstTimeUserEmojis.size() + repeatUserEmojis.size())
                            .build());
                    newFeedResDto.setPlaceWithTimeInfo(PlaceWithTimeResDto.builder()
                            .placeIdx(currentPlace.getId())
                            .placeName(currentPlace.getName())
                            .time(firstBrush.getCreatedAt().withNano(0))
                            .build());
                    newFeedResDtos.add(newFeedResDto);

                    firstTimeUserEmojis = new ArrayList<>();
                    repeatUserEmojis = new ArrayList<>();
                    userIdxSet = new HashSet<>();
                }

                if (brush.getId().equals(-1L)) {
                    break;
                }

                // 첫 스침 갱신
                firstBrush = brush;

                // 현재 위치 갱신
                currentPlace = brush.getPlace();

            }

            if (!userIdxSet.contains(message.getUser().getId())) {
                Long cnt = brushRepository.countByUser1IdAndUser2IdAndCreatedAtIsBefore(smallIdx, bigIdx, LocalDateTime.now().minusDays(1));
                if (cnt.equals(0L)) {
                    firstTimeUserEmojis.add(message.getUser().getEmoji());
                } else {
                    repeatUserEmojis.add(message.getUser().getEmoji());
                }
                userIdxSet.add(message.getUser().getId());
            }

        }

        return newFeedResDtos;

    }

    @AllArgsConstructor
    @Getter
    private static class BrushInfo {
        private Long myMessageIdx;
        private Long oppositeMessageIdx;
        private LocalDateTime createdAt;
    }

    @AllArgsConstructor
    @Getter
    private static class BrushWithPlaceInfo {
        private Long myMessageIdx;
        private Long oppositeMessageIdx;
        private Long placeIdx;
        private LocalDateTime createdAt;
    }
}
