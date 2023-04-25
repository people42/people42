package com.fourtytwo.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.auth.RefreshTokenProvider;
import com.fourtytwo.dto.user.*;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Null;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final MessageRepository messageRepository;

    private final InputStream is = UserService.class.getResourceAsStream("/word_set.json");
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    // Gson 라이브러리로 JSON 파싱
    private final Gson gson = new Gson();
    private final JsonObject json = gson.fromJson(reader, JsonObject.class);

    // 필요한 데이터 추출
    private final String[] nouns = gson.fromJson(json.get("nouns"), String[].class);
    private final String[] adjectives = gson.fromJson(json.get("adjectives"), String[].class);

    private HashSet<String> nicknames = new HashSet<>();
    private final RedisTemplate<String, String> redisTemplate;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                       MessageRepository messageRepository, RefreshTokenProvider refreshTokenProvider,
                       RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messageRepository = messageRepository;
        this.refreshTokenProvider = refreshTokenProvider;
        this.redisTemplate = redisTemplate;
    }


    public LoginResponseDto googleLogin(String o_auth_token) {

        ResponseEntity<?> response = this.checkGoogleToken(o_auth_token);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
        }
        GoogleOAuthResponseDto googleOAuthResponse = (GoogleOAuthResponseDto) response.getBody();

        String userEmail = "google" + "_" + googleOAuthResponse.getEmail();
        User foundUser = userRepository.findByEmailAndIsActiveTrue(userEmail);
        if (foundUser == null) {
            return new LoginResponseDto(null, googleOAuthResponse.getEmail(), null, null, null);
        }
        String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        String refreshToken = refreshTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        redisTemplate.opsForHash().put("refresh", refreshToken, foundUser.getId().toString());
        return new LoginResponseDto(foundUser.getId(), foundUser.getEmail(), foundUser.getNickname(), accessToken, refreshToken);
    }

    public LoginResponseDto appleLogin(String idToken) {
        // idToken 검증 및 유저 정보 추출
        ResponseEntity<?> response = checkAppleToken(idToken);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
        }
        AppleOAuthResponseDto appleOAuthResponse = (AppleOAuthResponseDto) response.getBody();

        String userEmail = "apple" + "_" + appleOAuthResponse.getEmail();
        User foundUser = userRepository.findByEmailAndIsActiveTrue(userEmail);
        if (foundUser == null) {
            // 회원가입 처리가 필요한 경우
            return new LoginResponseDto(null, appleOAuthResponse.getEmail(), null, null, null);
        }

        String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        String refreshToken = refreshTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        redisTemplate.opsForHash().put("refresh", refreshToken, foundUser.getId().toString());
        return new LoginResponseDto(foundUser.getId(), foundUser.getEmail(), foundUser.getNickname(), accessToken, refreshToken);
    }

    public LoginResponseDto signup(SignupRequestDto signupRequestDto, String socialType) {

        if (socialType.equals("google")) {
            ResponseEntity<?> response = this.checkGoogleToken(signupRequestDto.getO_auth_token());
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
            }
        } else {
            ResponseEntity<?> response = this.checkAppleToken(signupRequestDto.getO_auth_token());
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
            }
        }

        if (userRepository.findByNickname(signupRequestDto.getNickname()) != null) {
            throw new DataIntegrityViolationException("이미 존재하는 닉네임입니다.");
        }

        User foundUser = userRepository.findByEmail(socialType + "_" + signupRequestDto.getEmail());
        if (foundUser != null) {
            if (foundUser.getIsActive()) {
                throw new DataIntegrityViolationException("이미 존재하는 이메일입니다.");
            } else {
                foundUser.setIsActive(true);
                foundUser.setEmail(socialType + "_" + signupRequestDto.getEmail());
                foundUser.setNickname(signupRequestDto.getNickname());
                User savedUser = userRepository.save(foundUser);
                String accessToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
                String refreshToken = refreshTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
                redisTemplate.opsForHash().put("refresh", refreshToken, savedUser.getId().toString());
                return new LoginResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), accessToken, refreshToken);
            }
        }

        User newUser = new User();
        newUser.setEmail(socialType + "_" + signupRequestDto.getEmail());
        newUser.setNickname(signupRequestDto.getNickname());
        if (newUser.getNickname().equals("admin")) {
            newUser.setRoles("ROLE_ADMIN");
        } else {
            newUser.setRoles("ROLE_USER");
        }
        newUser.setIsActive(true);
        User savedUser = userRepository.save(newUser);
        String accessToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
        String refreshToken = refreshTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
        redisTemplate.opsForHash().put("refresh", refreshToken, savedUser.getId().toString());
        return new LoginResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), accessToken, refreshToken);
    }

    public ResponseEntity<?> checkGoogleToken(String access_token) {
        String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl(GOOGLE_USERINFO_REQUEST_URL)
                    .defaultHeader("Authorization", "Bearer " + access_token)
                    .build();
            GoogleOAuthResponseDto res = webClient.get().retrieve().bodyToMono(GoogleOAuthResponseDto.class).block();
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    public ResponseEntity<?> checkAppleToken(String id_token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(id_token);
            String keyId = decodedJWT.getKeyId();

            JwkProvider provider = new JwkProviderBuilder("https://appleid.apple.com/auth/keys").build();
            Jwk jwk = provider.get(keyId);

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .build();

            DecodedJWT jwt = verifier.verify(id_token);

            // 이메일과 사용자 식별자를 얻을 수 있습니다.
            String email = jwt.getClaim("email").asString();
            String userIdentifier = jwt.getSubject();

            AppleOAuthResponseDto response = new AppleOAuthResponseDto();
            response.setSub(userIdentifier);
            response.setEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    public NicknameResDto createNickname() {
        while (true) {
            Random random = new Random();
            String adjective = adjectives[random.nextInt(adjectives.length)];
            String noun = nouns[random.nextInt(nouns.length)];
            String nickname = adjective + " " + noun;
            if (!nicknames.contains(nickname)){
                NicknameResDto nicknameResDto = new NicknameResDto(nickname);
                nicknames.add(nickname);
                return nicknameResDto;
            }
        }
    }

    public void deleteUser(String accessToken) {
        // maria db에서 탈퇴 처리
        User user = this.checkUser(accessToken);
        user.setIsActive(false);
        userRepository.save(user);

        // refresh token 삭제
        Set<Object> keys =  redisTemplate.opsForHash().keys("refresh");
        for (Object key : keys) {
            Long userIdx = Long.parseLong((String) redisTemplate.opsForHash().get("refresh", key));
            if (Objects.equals(userIdx, user.getId())) {
                redisTemplate.opsForHash().delete("refresh", key);
                break;
            }
        }
    }

    public MyInfoResDto getMyInfo(String accessToken) {
        User user = this.checkUser(accessToken);
        String emoji = userRepository.findEmojiById(user.getId());
        String message = messageRepository.findFirstContentByUserOrderByCreatedAtDesc(user);
        Long messageCnt = messageRepository.findTodayCountByUser(user);
        return MyInfoResDto.builder()
                .emoji(emoji)
                .message(message)
                .messageCnt(messageCnt)
                .build();
    }

    public User checkUser(String accessToken) {
        User user = jwtTokenProvider.getUser(accessToken);
        if (user == null || !user.getIsActive()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        return user;
    }

    public AccessTokenResDto getAccessToken(String refreshToken) {
        if (redisTemplate.opsForHash().get("refresh", refreshToken) == null) {
            throw new EntityNotFoundException("존재하지 않는 토큰입니다.");
        }
        Long userIdx = Long.parseLong((String) redisTemplate.opsForHash().get("refresh", refreshToken));
        Optional<User> user = userRepository.findById(userIdx);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        if (!refreshTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationServiceException("토큰이 만료된 유저입니다.");
        }

        String accessToken = jwtTokenProvider.createToken(user.get().getId(), user.get().getRoleList());
        return new AccessTokenResDto(accessToken);
    }
}
