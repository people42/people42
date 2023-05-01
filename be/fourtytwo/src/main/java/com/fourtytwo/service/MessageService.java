package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.message.MessageDeleteReqDto;
import com.fourtytwo.dto.message.MyMessageHistoryResDto;
import com.fourtytwo.dto.user.ChangeEmojiReqDto;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.emotion.EmotionRepository;
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExpressionRepository expressionRepository;
    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;

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
        if (user == null) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        } else if (!user.getIsActive()) {
            throw new EntityNotFoundException("삭제된 유저입니다.");
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
                    .messageIdx(message.getId())
                    .fire(fire)
                    .tear(tear)
                    .thumbsUp(thumbsUp)
                    .heart(heart)
                    .build();
            myMessageHistoryResDtos.add(myMessageHistoryResDto);
        }
        return myMessageHistoryResDtos;
    }

    public void deleteMessage(String accessToken, MessageDeleteReqDto messageDeleteReqDto) {
        User user = this.checkUser(accessToken);
        Optional<Message> message = messageRepository.findById(messageDeleteReqDto.getMessageIdx());
        if (message.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 메시지입니다.");
        } else if (!message.get().getIsActive()) {
            throw new EntityNotFoundException("이미 삭제된 메시지입니다.");
        } else if (!message.get().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("요청한 유저의 메시지가 아닙니다.");
        }

        message.get().setIsActive(false);
        messageRepository.save(message.get());
    }

    public void changeEmoji(String accessToken, ChangeEmojiReqDto changeEmojiReqDto) {
        User user = this.checkUser(accessToken);
        user.setEmoji(changeEmojiReqDto.getEmoji());
        userRepository.save(user);
    }

}
