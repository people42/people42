package com.fourtytwo;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.controller.FeedController;
import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.Place;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.brush.BrushRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.place.PlaceRepository;
import com.fourtytwo.repository.user.UserRepository;
import com.fourtytwo.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FeedControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/feed";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private BrushRepository brushRepository;

    @Autowired
    private FeedService feedService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static User savedUser1;
    private static User savedUser2;
    private static User savedUser3;
    private static String accessToken;
    private static Place savedPlace1;
    private static Brush savedBrush1;
    private static Brush savedBrush3;

    @BeforeEach
    void beforeAll() {
        User user1 = User.builder()
                .email("email1")
                .color(0)
                .roles("USER")
                .emoji("happy")
                .isActive(true)
                .nickname("nickname1")
                .build();
        savedUser1 = userRepository.save(user1);


        User user2 = User.builder()
                .email("email2")
                .color(0)
                .roles("USER")
                .emoji("sad")
                .isActive(true)
                .nickname("nickname2")
                .build();
        savedUser2 = userRepository.save(user2);

        User user3 = User.builder()
                .email("email3")
                .color(0)
                .roles("USER")
                .emoji("confused")
                .isActive(true)
                .nickname("nickname3")
                .build();
        savedUser3 = userRepository.save(user3);

        Message message1 = Message.builder()
                .user(savedUser1)
                .content("user1이 쓴 메시지")
                .isActive(true)
                .build();
        Message savedMessage1 = messageRepository.save(message1);

        Message message2 = Message.builder()
                .user(savedUser2)
                .content("user2이 쓴 메시지")
                .isActive(true)
                .build();
        Message savedMessage2 = messageRepository.save(message2);

        Message message3 = Message.builder()
                .user(savedUser3)
                .content("user3이 쓴 메시지")
                .isActive(true)
                .build();
        Message savedMessage3 = messageRepository.save(message3);

        Place place1 = Place.builder()
                .name("장소1")
                .latitude(39.12345)
                .longitude(164.12345)
                .build();
        savedPlace1 = placeRepository.save(place1);

        Place place2 = Place.builder()
                .name("장소2")
                .latitude(39.56789)
                .longitude(164.56789)
                .build();
        Place savedPlace2 = placeRepository.save(place2);

        Brush brush1 = Brush.builder()
                .user1(savedUser1)
                .user2(savedUser2)
                .message1(savedMessage1)
                .message2(savedMessage2)
                .place(savedPlace1)
                .build();
        savedBrush1 = brushRepository.save(brush1);

        Brush brush2 = Brush.builder()
                .user1(savedUser1)
                .user2(savedUser2)
                .message1(savedMessage1)
                .message2(savedMessage2)
                .place(savedPlace2)
                .build();
        Brush savedBrush2 = brushRepository.save(brush2);

        Brush brush3 = Brush.builder()
                .user1(savedUser1)
                .user2(savedUser3)
                .message1(savedMessage1)
                .message2(savedMessage3)
                .place(savedPlace2)
                .build();
        savedBrush3 = brushRepository.save(brush3);

        accessToken = jwtTokenProvider.createToken(savedUser1.getId(), savedUser1.getRoleList());

    }

    @Test
    void 최근피드조회() throws Exception {
        mockMvc.perform(get(BASE_URL + "/recent").header("ACCESS-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void 장소피드조회() throws Exception {
        mockMvc.perform(get(BASE_URL + "/place?placeIdx=" + savedBrush3.getPlace().getId()
                        + "&time=" + savedBrush3.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + "&page=0&size=2")
                        .header("ACCESS-TOKEN", accessToken))
                .andExpect(status().isOk());
    }

}
