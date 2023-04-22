package com.fourtytwo;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
public class AccountControllerTest {

//    @Autowired
//    MockMvc mockMvc;
//
//    private static final String BASE_URL = "/api/v1/account";
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @BeforeEach
//    void beforeEach() {
//    }
//
//    @Test
//    void 회원탈퇴() throws Exception {
//        User user = User.builder()
//                .roles("ROLE_USER")
//                .isActive(true)
//                .build();
//        userRepository.save(user);
//        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getRoleList());
//
//        // 회원탈퇴
//        mockMvc.perform(put(BASE_URL + "/withdrawal").header("ACCESS-TOKEN", accessToken))
//                .andExpect(status().isOk());
//
//        // 이미 탈퇴한 회원에 또 요청한 경우
//        mockMvc.perform(put(BASE_URL + "/withdrawal").header("ACCESS-TOKEN", accessToken))
//                .andExpect(status().isNotFound());
//    }

}
