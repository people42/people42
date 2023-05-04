package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.alert.AlertCntResDto;
import com.fourtytwo.dto.alert.AlertHistoryResDto;
import com.fourtytwo.entity.Expression;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.expression.ExpressionRepository;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AlertService {

    private final JwtTokenProvider jwtTokenProvider;

    private final ExpressionRepository expressionRepository;

    public AlertCntResDto getMyAlertCnt(String accessToken) {
        User user = checkUser(accessToken);
        return AlertCntResDto.builder()
                .alertCnt(expressionRepository.countByMessageUserAndIsReadIsFalseAndMessage_IsInappropriateIsFalse(user))
                .build();
    }

    public List<AlertHistoryResDto> getMyAlertHistory(String accessToken) {
        User user = checkUser(accessToken);
        List<Expression> expressionList = expressionRepository.MessageUserAndIsReadIsFalseAndMessage_IsInappropriateIsFalse(user);
        List<AlertHistoryResDto> alertHistoryResDtos = new ArrayList<>();
        for (Expression expression : expressionList) {
            if (!expression.getMessage().getIsActive()) {
                continue;
            }
            alertHistoryResDtos.add(AlertHistoryResDto.builder()
                    .title("누군가 당신의 메시지에 감정을 표현했어요")
                    .body(expression.getMessage().getContent())
                    .emoji(expression.getEmotion().getName())
                    .createdAt(expression.getCreatedAt().withNano(0))
                    .build());
            expression.setIsRead(true);
            expressionRepository.save(expression);
        }
        return alertHistoryResDtos;
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
