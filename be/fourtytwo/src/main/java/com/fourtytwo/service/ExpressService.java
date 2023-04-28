package com.fourtytwo.service;

import com.auth0.jwt.JWT;
import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.feed.ExpressReqDto;
import com.fourtytwo.entity.Emotion;
import com.fourtytwo.entity.Expression;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.emotion.EmotionRepository;
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExpressService {

    private final ExpressionRepository expressionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EmotionRepository emotionRepository;

    public void express(String accessToken, ExpressReqDto expressReqDto) {
        User user = this.checkUserByAccessToken(accessToken);
        Optional<Message> message = messageRepository.findById(expressReqDto.getMessageIdx());
        if (message.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 메시지입니다.");
        } else if (!message.get().getIsActive()) {
            throw new EntityNotFoundException("삭제된 메시지입니다.");
        }

        Optional<Emotion> emotion = emotionRepository.findByName(expressReqDto.getEmotion());
        if (emotion.isEmpty() && !expressReqDto.getEmotion().equals("delete")) {
            throw new EntityNotFoundException("감정은 [heart, fire, tear, thumbsUp, delete]만 가능합니다.");
        }

        Optional<Expression> expression = expressionRepository.findByMessageAndUserId(message.get(), user.getId());
        if (expression.isEmpty()) {
            if (!expressReqDto.getEmotion().equals("delete")) {
                Expression newExpression = Expression.builder()
                        .emotion(emotion.get())
                        .message(message.get())
                        .user(user)
                        .build();
                expressionRepository.save(newExpression);
            }
        } else {
            if (expressReqDto.getEmotion().equals("delete")) {
                expressionRepository.delete(expression.get());
            } else {
                expression.get().setEmotion(emotion.get());
                expressionRepository.save(expression.get());
            }
        }
    }

    private User checkUserByAccessToken(String accessToken) {
        User user = jwtTokenProvider.getUser(accessToken);
        if (user == null) {
            throw new EntityNotFoundException("유저가 존재하지 않습니다.");
        } else if (!user.getIsActive()) {
            throw new EntityNotFoundException("삭제된 유저입니다.");
        }
        return user;
    }

}
