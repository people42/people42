package com.fourtytwo.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 중지
@Service
public class FcmService {

//    @Value("${fcm.key.path}")
//    private String FCM_PRIVATE_KEY_PATH;
//
//    @Value("${fcm.key.scope}")
//    private String fireBaseScope;

    // fcm 기본 설정 진행
//    @PostConstruct
//    public void init() {
//        try {
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(
//                            GoogleCredentials
//                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
//                                    .createScoped(List.of(fireBaseScope)))
//                    .build();
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    // 알림 보내기
    public void sendByTokenList(List<String> tokenList) {

        // 메시지 만들기
        List<Message> messages = tokenList.stream().map(token -> Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .setNotification(Notification.builder()
                        .setTitle("제목")
                        .setBody("내용")
                        .build())
                .setToken(token)
                .build()).collect(Collectors.toList());

        // 요청에 대한 응답을 받을 response
        BatchResponse response;
        try {
            // 알림 발송
            response = FirebaseMessaging.getInstance().sendAll(messages);

            // 요청에 대한 응답 처리
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                System.out.println("실패한 토큰들: " + failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            System.out.println("메시지 전송 실패");
        }
    }

}
