package com.fourtytwo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.equalTo;

  @SpringBootTest
  @AutoConfigureMockMvc
  @Transactional
public class AccountControllerTest {

      @Autowired
      MockMvc mockMvc;

      private static final String BASE_URL = "/api/v1/account";

      @Autowired
      private UserRepository userRepository;

      @Autowired
      private MessageRepository messageRepository;

      @Autowired
      private JwtTokenProvider jwtTokenProvider;

      @Autowired
      private ObjectMapper objectMapper;

      @BeforeEach
      void beforeEach() {
      }

      @Test
      void 회원탈퇴() throws Exception {
          User user = User.builder()
                  .roles("ROLE_USER")
                  .isActive(true)
                  .build();
          userRepository.save(user);
          String accessToken = jwtTokenProvider.createToken(user.getId(), user.getRoleList());

          // 회원탈퇴
          mockMvc.perform(put(BASE_URL + "/withdrawal").header("ACCESS-TOKEN", accessToken))
                  .andExpect(status().isOk());

          // 이미 탈퇴한 회원에 또 요청한 경우
          mockMvc.perform(put(BASE_URL + "/withdrawal").header("ACCESS-TOKEN", accessToken))
                  .andExpect(status().isNotFound());
      }

      @Test
      void 내정보조회() throws Exception {
          User user = User.builder()
                  .roles("ROLE_USER")
                  .isActive(true)
                  .emoji("이모지")
                  .build();
          User savedUser = userRepository.save(user);
          String accessToken = jwtTokenProvider.createToken(savedUser.getId(), user.getRoleList());

          Message message1 = Message.builder()
                  .content("오랜된 메시지")
                  .isActive(true)
                  .user(savedUser)
                  .build();
          messageRepository.save(message1);

          Thread.sleep(1000);

          Message message2 = Message.builder()
                  .content("최근 메시지")
                  .isActive(true)
                  .user(savedUser)
                  .build();
          messageRepository.save(message2);

          // 가장 최근 메시지가 조회되는지 확인
          mockMvc.perform(get(BASE_URL + "/myinfo").header("ACCESS-TOKEN", accessToken))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.data.message").value("최근 메시지"))
                  .andExpect(jsonPath("$.data.emoji").value("이모지"));

          User user2 = User.builder()
                  .roles("ROLE_USER")
                  .isActive(true)
                  .emoji("이모지")
                  .build();
          User savedUser2 = userRepository.save(user2);
          String accessToken2 = jwtTokenProvider.createToken(savedUser2.getId(), user.getRoleList());

          // 메시지가 없는 유저 조회
          mockMvc.perform(get(BASE_URL + "/myinfo").header("ACCESS-TOKEN", accessToken2))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.data.message").value(nullValue()))
                  .andExpect(jsonPath("$.data.emoji").value("이모지"));
      }

      @Test
      void 상태메세지등록() throws Exception {
          User user = User.builder()
                  .roles("ROLE_USER")
                  .isActive(true)
                  .build();
          User savedUser = userRepository.save(user);
          String accessToken = jwtTokenProvider.createToken(savedUser.getId(), user.getRoleList());

          // 정상적인 요청
          Map<String, String> body1 = new HashMap<>();
          body1.put("message", "새로운 상태메시지");
          mockMvc.perform(post(BASE_URL + "/message")
                          .header("ACCESS-TOKEN", accessToken)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(body1)))
                  .andExpect(status().isOk());

          // 메시지가 비어있는 경우
          Map<String, String> body2 = new HashMap<>();
          body2.put("message", "");
          mockMvc.perform(post(BASE_URL + "/message")
                          .header("ACCESS-TOKEN", accessToken)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(body2)))
                  .andExpect(status().isBadRequest());
      }

      @Test
      void 신고() throws Exception {
          User user1 = User.builder()
                  .roles("ROLE_USER")
                  .isActive(true)
                  .build();
          User savedUser1 = userRepository.save(user1);
          String accessToken1 = jwtTokenProvider.createToken(savedUser1.getId(), savedUser1.getRoleList());

          User user2 = User.builder()
                  .roles("ROLE_USER")
                  .isActive(true)
                  .build();
          User savedUser2 = userRepository.save(user2);

          Message message = Message.builder()
                  .user(savedUser2)
                  .content("신고당할 메시지")
                  .isActive(true)
                  .build();
          Message savedMessage = messageRepository.save(message);

          // 정상적인 요청
          Map<String, String> body1 = new HashMap<>();
          body1.put("messageIdx", savedMessage.getId().toString());
          body1.put("content", "신고 내용");
          mockMvc.perform(post(BASE_URL + "/report")
                          .header("ACCESS-TOKEN", accessToken1)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(body1)))
                  .andExpect(status().isOk());

          // 한 명이 한 메시지를 여러번 신고한 경우
          Map<String, String> body2 = new HashMap<>();
          body2.put("messageIdx", savedMessage.getId().toString());
          body2.put("content", "신고 내용");
          mockMvc.perform(post(BASE_URL + "/report")
                          .header("ACCESS-TOKEN", accessToken1)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(objectMapper.writeValueAsString(body2)))
                  .andExpect(status().isConflict());
      }

}
