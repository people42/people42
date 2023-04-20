package com.fourtytwo.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.user.AppleOAuthResponseDto;
import com.fourtytwo.dto.user.GoogleOAuthResponseDto;
import com.fourtytwo.dto.user.LoginResponseDto;
import com.fourtytwo.dto.user.SignupRequestDto;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.EntityNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponseDto googleLogin(String o_auth_token) {

        ResponseEntity<?> response = this.checkGoogleToken(o_auth_token);
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
        }
        GoogleOAuthResponseDto googleOAuthResponse = (GoogleOAuthResponseDto) response.getBody();

        String userEmail = googleOAuthResponse.getEmail();
        User foundUser = userRepository.findByEmailAndIsActiveTrue(userEmail);
        if (foundUser == null) {
            return new LoginResponseDto(null, googleOAuthResponse.getEmail(), null, null);
        }
        String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        return new LoginResponseDto(foundUser.getId(), foundUser.getEmail(), foundUser.getNickname(), accessToken);
    }

    public LoginResponseDto appleLogin(String idToken) {
        // idToken 검증 및 유저 정보 추출
        ResponseEntity<?> response = checkAppleToken(idToken);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
        }
        AppleOAuthResponseDto appleOAuthResponse = (AppleOAuthResponseDto) response.getBody();

        String userEmail = appleOAuthResponse.getEmail();
        User foundUser = userRepository.findByEmailAndIsActiveTrue(userEmail);
        if (foundUser == null) {
            // 회원가입 처리가 필요한 경우
            return new LoginResponseDto(null, appleOAuthResponse.getEmail(), null, null);
        }

        String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        return new LoginResponseDto(foundUser.getId(), foundUser.getEmail(), foundUser.getNickname(), accessToken);
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

        User foundUser = userRepository.findByEmail(signupRequestDto.getEmail());
        if (foundUser != null) {
            if (foundUser.getIsActive()) {
                throw new DataIntegrityViolationException("이미 존재하는 이메일입니다.");
            } else {
                foundUser.setIsActive(true);
                foundUser.setEmail(signupRequestDto.getEmail());
                foundUser.setNickname(signupRequestDto.getNickname());
                User savedUser = userRepository.save(foundUser);
                String accessToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
                return new LoginResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), accessToken);
            }
        }

        User newUser = new User();
        newUser.setEmail(signupRequestDto.getEmail());
        newUser.setNickname(signupRequestDto.getNickname());
        if (newUser.getNickname().equals("admin")) {
            newUser.setRoles("ROLE_ADMIN");
        } else {
            newUser.setRoles("ROLE_USER");
        }
        newUser.setIsActive(true);
        User savedUser = userRepository.save(newUser);
        String accessToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
        return new LoginResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), accessToken);
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

    public void deleteUser(Long user_idx) {
        User foundUser = userRepository.findByIdAndIsActiveTrue(user_idx);
        if (foundUser == null) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        foundUser.setIsActive(false);
        userRepository.save(foundUser);
    }
}
