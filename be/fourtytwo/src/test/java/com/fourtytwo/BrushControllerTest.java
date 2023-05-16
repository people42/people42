package com.fourtytwo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.place.GpsReqDto;
import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import com.fourtytwo.service.GpsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
//@AutoConfigureMockMvc
public class BrushControllerTest {

//    @Autowired
//    MockMvc mockMvc;
//
//    private static final String BASE_URL = "/api/v1/background";
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private MessageRepository messageRepository;
//
//    @Autowired
//    private BrushRepository brushRepository;
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    private GpsService gpsService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//
//
//
//    @Test
//    void 백그라운드요청동시성테스트() throws Exception {
//
//        final User savedUser1 = userRepository.save(User.builder()
//                .email("email1")
//                .roles("ROLE_USER")
//                .nickname("nickname1")
//                .isActive(true)
//                .emoji("emoji1")
//                .build());
//        String accessToken1 = jwtTokenProvider.createToken(savedUser1.getId(), savedUser1.getRoleList());
//        messageRepository.save(Message.builder()
//                .user(savedUser1)
//                .content("user1이 쓴 메시지")
//                .isActive(true)
//                .isInappropriate(false)
//                .build());
//
//        final User savedUser2 = userRepository.save(User.builder()
//                .email("email2")
//                .roles("ROLE_USER")
//                .nickname("nickname2")
//                .isActive(true)
//                .emoji("emoji2")
//                .build());
//        String accessToken2 = jwtTokenProvider.createToken(savedUser2.getId(), savedUser2.getRoleList());
//        messageRepository.save(Message.builder()
//                .user(savedUser2)
//                .content("user2가 쓴 메시지")
//                .isActive(true)
//                .isInappropriate(false)
//                .build());
//
//        Map<String, Double> body = new HashMap<>();
//        body.put("latitude", 36.3553309);
//        body.put("longitude", 127.2980942);
//
//        mockMvc.perform(post(BASE_URL)
//                        .header("ACCESS-TOKEN", accessToken2)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andExpect(status().isOk());
//
//        int threadCount = 80;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    mockMvc.perform(post(BASE_URL)
//                                    .header("ACCESS-TOKEN", accessToken1)
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .content(objectMapper.writeValueAsString(body)))
//                            .andExpect(status().isOk());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await();
//
//        List<Brush> brush = brushRepository.findBrushesByUser1IdAndUser2Id(savedUser1.getId(), savedUser2.getId());
//
//        Assertions.assertThat(brush.size()).isEqualTo(1L);
//
//    }

}
