package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.message.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

}
