package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.alert.AlertCntResDto;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.user.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 중지
@Service
public class FcmService {

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    @Value("${fcm.key.scope}")
    private String fireBaseScope;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExpressionRepository expressionRepository;

    public FcmService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, ExpressionRepository expressionRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.expressionRepository = expressionRepository;
    }

    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .createScoped(List.of(fireBaseScope)))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 유저 여러명에게 알림 보내기
    public void sendToUserList(List<User> userList, String title, String body, String image) {
        
        // 현재 토큰이 유효한 유저에게만 메시지 전송
        List<Message> messages = new ArrayList<>();
        List<User> validUserList = new ArrayList<>();
        for (User user : userList) {
            if (user.getFcmToken() != null && user.getFcmTokenExpirationDateTime().isAfter(LocalDateTime.now())) {
                messages.add(Message.builder()
                        .putData("time", LocalDateTime.now().toString())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setImage(image)
                                .build())
                        .setToken(user.getFcmToken())
                        .build());
                validUserList.add(user);
            }
        }

        // 요청에 대한 응답을 받을 response
        BatchResponse response;
        try {
            // 알림 발송
            response = FirebaseMessaging.getInstance().sendAll(messages);

            // 요청에 대한 응답 처리
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();

                // 토큰이 유효하지 않은 유저에 대해 DB 갱신
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        validUserList.get(i).setFcmToken(null);
                        validUserList.get(i).setFcmTokenExpirationDateTime(null);
                    }
                }
            }
        } catch (FirebaseMessagingException e) {
            System.out.println("메시지 전송 실패");
        }
    }

    // 유저 한 명에게 알림 보내기
    public void sendToUser(User user, String title, String body, String image) {

        if (user.getFcmToken() == null || !user.getFcmTokenExpirationDateTime().isAfter(LocalDateTime.now())) {
            return;
        }

        // 현재 토큰이 유효한 유저에게만 메시지 전송
        Message message = Message.builder()
//                .putData("time", LocalDateTime.now().toString())
//                .setNotification(Notification.builder()
//                        .setTitle(title)
//                        .setBody(body)
//                        .setImage(image)
//                        .build())
                .setToken(user.getFcmToken())
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .setIcon(image)
                                .build())
                        .setFcmOptions(WebpushFcmOptions.builder()
                                .setLink("https://www.people42.com")
                                .build())
                        .build())
                .build();

        try {
            // 알림 발송
            FirebaseMessaging.getInstance().send(message);

        } catch (FirebaseMessagingException e) {
            System.out.println("메시지 전송 실패");
            user.setFcmToken(null);
            user.setFcmTokenExpirationDateTime(null);
        }
    }

    // FCM 토큰 갱신
    public void updateFcmToken(String accessToken, String fcmToken) {
        User user = checkUser(accessToken);

        // 기존에 같은 기기에서 사용중이던 유저 있으면 해당 유저는 토큰 삭제
        Optional<User> originalUser = userRepository.findByFcmToken(fcmToken);
        if (originalUser.isPresent()) {
            originalUser.get().setFcmToken(null);
            originalUser.get().setFcmTokenExpirationDateTime(null);
        }

        user.setFcmToken(fcmToken);
        user.setFcmTokenExpirationDateTime(LocalDateTime.now().plusMonths(2));
        userRepository.save(user);
    }

    public AlertCntResDto getMyAlertCnt(String accessToken) {
        User user = checkUser(accessToken);
        return AlertCntResDto.builder()
                .alertCnt(expressionRepository.countByMessageUserAndIsReadIsFalse(user))
                .build();
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
