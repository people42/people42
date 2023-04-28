package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.message.MyMessageHistoryResDto;
import com.fourtytwo.entity.Emotion;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.EmotionRepository;
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.message.MessageRepository;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Expr;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExpressionRepository expressionRepository;
    private final EmotionRepository emotionRepository;

    public void createMessage(String accessToken, String message) {
        User user = this.checkUser(accessToken);
        Message newMessage = Message.builder()
                .user(user)
                .content(message)
                .isActive(true)
                .build();
        messageRepository.save(newMessage);
    }

    public User checkUser(String accessToken) {
        User user = jwtTokenProvider.getUser(accessToken);
        if (user == null || !user.getIsActive()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        return user;
    }

    public List<MyMessageHistoryResDto> getMyMessageHistoryByDate(String accessToken, LocalDate date) {
        User user = this.checkUser(accessToken);

        List<Message> messageList = messageRepository.findMessagesByUserAndCreatedAt(user, date);
        List<MyMessageHistoryResDto> myMessageHistoryResDtos = new ArrayList<>();
        for (Message message : messageList) {
            Long fire = expressionRepository.countByMessageAndEmotionName(message, "fire");
            Long tear = expressionRepository.countByMessageAndEmotionName(message, "tear");
            Long thumbsUp = expressionRepository.countByMessageAndEmotionName(message, "thumbsUp");
            Long heart = expressionRepository.countByMessageAndEmotionName(message, "heart");
            MyMessageHistoryResDto myMessageHistoryResDto = MyMessageHistoryResDto.builder()
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .fire(fire)
                    .tear(tear)
                    .thumbsUp(thumbsUp)
                    .heart(heart)
                    .build();
            myMessageHistoryResDtos.add(myMessageHistoryResDto);
        }
        return myMessageHistoryResDtos;
    }

}
