package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.notification.NotificationCntResDto;
import com.fourtytwo.dto.notification.NotificationHistoryResDto;
import com.fourtytwo.entity.Expression;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.expression.ExpressionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final JwtTokenProvider jwtTokenProvider;

    private final ExpressionRepository expressionRepository;

    public NotificationCntResDto getMyNotificationCnt(String accessToken) {
        User user = checkUser(accessToken);
        return NotificationCntResDto.builder()
                .notificationCnt(expressionRepository.countByMessageUserAndIsReadIsFalseAndMessage_IsInappropriateIsFalseAndMessage_IsActiveIsTrue(user))
                .build();
    }

    public List<NotificationHistoryResDto> getMyNotificationHistory(String accessToken) {
        User user = checkUser(accessToken);
        List<Expression> expressionList = expressionRepository.findByMessageUserAndIsReadIsFalseAndMessage_IsInappropriateIsFalseAndMessage_IsActiveIsTrue(user);
        List<NotificationHistoryResDto> notificationHistoryResDtos = new ArrayList<>();
        for (Expression expression : expressionList) {

            notificationHistoryResDtos.add(NotificationHistoryResDto.builder()
                    .title("누군가 감정을 표현했어요")
                    .body(expression.getMessage().getContent())
                    .emoji(expression.getEmotion().getName())
                    .createdAt(expression.getCreatedAt().withNano(0))
                    .build());
            expression.setIsRead(true);
            expressionRepository.save(expression);
        }
        return notificationHistoryResDtos;
    }

    private User checkUser(String accessToken) {
        User user = jwtTokenProvider.getUser(accessToken);
        if (user == null) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        } else if (!user.getIsActive()) {
            throw new EntityNotFoundException("삭제된 유저입니다.");
        }
        return user;
    }

}
