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
import com.fourtytwo.repository.expression.ExpressionRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.management.openmbean.InvalidKeyException;
import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final MessageRepository messageRepository;
    private final ExpressionRepository expressionRepository;

    private final InputStream is = UserService.class.getResourceAsStream("/word_set.json");
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    // Gson 라이브러리로 JSON 파싱
    private final Gson gson = new Gson();
    private final JsonObject json = gson.fromJson(reader, JsonObject.class);

    // 필요한 데이터 추출
    private final String[] nouns = gson.fromJson(json.get("nouns"), String[].class);
    private final String[] adjectives = gson.fromJson(json.get("adjectives"), String[].class);

    private final String appleTeamId;
    private final String appleKeyId;
    private final String appleClientId;
    private final String appleClientAppId;
    private final String appleKeyPath;

    private final RedisTemplate<String, String> redisTemplate;
    private final List<String> colors;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                       MessageRepository messageRepository, RefreshTokenProvider refreshTokenProvider,
                       RedisTemplate<String, String> redisTemplate, List<String> colors, String appleTeamId,
                       String appleKeyId, String appleClientId, String appleKeyPath, String appleClientAppId,
                       ExpressionRepository expressionRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messageRepository = messageRepository;
        this.refreshTokenProvider = refreshTokenProvider;
        this.redisTemplate = redisTemplate;
        this.colors = new ArrayList<>(Arrays.asList("red", "orange", "yellow", "green", "sky", "blue", "purple", "pink"));
        this.appleTeamId = appleTeamId;
        this.appleKeyId = appleKeyId;
        this.appleClientId = appleClientId;
        this.appleClientAppId = appleClientAppId;
        this.appleKeyPath = appleKeyPath;
        this.expressionRepository = expressionRepository;
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
            return new LoginResponseDto(null, googleOAuthResponse.getEmail(), null, null, null, null, null, null);
        }
        String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        String refreshToken = refreshTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        redisTemplate.opsForHash().put("refresh", refreshToken, foundUser.getId().toString());
        return new LoginResponseDto(foundUser.getId(), foundUser.getEmail(), foundUser.getNickname(), foundUser.getEmoji(), foundUser.getColor(), accessToken, refreshToken, null);
    }

    public String getAppleToken(String appleCode, String tokenType, String domainType) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String clientId;
        if (domainType == "web" || domainType == "webDelete") {
            clientId = appleClientId;
        } else {
            clientId = appleClientAppId;
        }

        String appleClientSecret = generateClientSecret(clientId);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", appleCode);
        if (domainType == "web") {
            map.add("redirect_uri", "https://www.people42.com/be42/api/v1/auth/check/apple/web");
        } else if (domainType == "webDelete") {
            map.add("redirect_uri", "https://www.people42.com/withdrawal/apple");
        }
        else {
            map.add("redirect_uri", "https://www.people42.com/signin/apple");
        }
        map.add("client_id", clientId);
        map.add("client_secret", appleClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://appleid.apple.com/auth/token", request, String.class);

        String responseBody = response.getBody();
        System.out.println("애플 응답"+responseBody);
        JSONObject jsonObject = new JSONObject(responseBody);

        if (tokenType.equals("id")) {
            return jsonObject.getString("id_token");
        } else {
            return jsonObject.getString("access_token");
        }

    }

    public String generateClientSecret(String ClientId) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, InvalidKeyException {

        // Load the auth key file.
        InputStream inputStream = UserService.class.getClassLoader().getResourceAsStream(appleKeyPath);
        String authKey = new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1);

        // Extract the private key from the auth key.
        authKey = authKey.replace("-----BEGIN PRIVATE KEY-----\n", "");
        authKey = authKey.replace("-----END PRIVATE KEY-----", "");
        authKey = authKey.replaceAll("\\s", "");
        byte[] decodedAuthKey = Base64.getDecoder().decode(authKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedAuthKey);
        ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(keySpec);

        // Generate the client secret.
        String token = JWT.create()
                .withIssuer(appleTeamId)
                .withAudience("https://appleid.apple.com")
                .withSubject(ClientId)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .withIssuedAt(Date.from(Instant.now()))
                .sign(Algorithm.ECDSA256(privateKey));

        return token;
    }


    public LoginResponseDto appleLogin(String idToken) {
        // idToken 검증 및 유저 정보 추출
        ResponseEntity<?> response = checkAppleToken(idToken);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
        }
        AppleOAuthResponseDto appleOAuthResponse = (AppleOAuthResponseDto) response.getBody();

        String userEmail = "apple" + "_" + appleOAuthResponse.getEmail();
        User foundUser = userRepository.findByAppleId(appleOAuthResponse.getSub());
        if (foundUser == null) {
            // 회원가입 처리가 필요한 경우
            User newUser = User.builder()
                    .email(userEmail)
                    .appleId(appleOAuthResponse.getSub())
                    .isActive(false)
                    .build();
            userRepository.save(newUser);
            return new LoginResponseDto(newUser.getId(), appleOAuthResponse.getEmail(), null, null, null, null, null, idToken);
        }
        if (!foundUser.getIsActive()) {
            String returnEmail = userEmail.split("apple_")[1];
            return new LoginResponseDto(foundUser.getId(), returnEmail, null, null, null, null, null, idToken);
        }

        String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        String refreshToken = refreshTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
        redisTemplate.opsForHash().put("refresh", refreshToken, foundUser.getId().toString());
        return new LoginResponseDto(foundUser.getId(), foundUser.getEmail(), foundUser.getNickname(), foundUser.getEmoji(), foundUser.getColor(), accessToken, refreshToken, null);
    }

    public LoginResponseDto signup(SignupRequestDto signupRequestDto, String socialType) {

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        User foundUser = userRepository.findByEmail(socialType + "_" + signupRequestDto.getEmail());

        switch (socialType) {
            case "google": {
                ResponseEntity<?> response = this.checkGoogleToken(signupRequestDto.getO_auth_token());
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
                }
                break;
            }
            case "apple": {
                ResponseEntity<?> response = this.checkAppleToken(signupRequestDto.getO_auth_token());
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
                }
                AppleOAuthResponseDto appleOAuthResponse = (AppleOAuthResponseDto) response.getBody();
                foundUser = userRepository.findByAppleId(appleOAuthResponse.getSub());
                break;
            }
            case "androidGoogle": {
                ResponseEntity<?> response = this.checkAndroidGoogleIdToken(signupRequestDto.getO_auth_token());
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
                }
                break;
            }
            case "webApple": {
                try {
                    String appleIdToken = this.getAppleToken(signupRequestDto.getO_auth_token(), "id", "web");
                    ResponseEntity<?> response = this.checkAppleToken(appleIdToken);
                    if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
                    }
                    AppleOAuthResponseDto appleOAuthResponse = (AppleOAuthResponseDto) response.getBody();
                    foundUser = userRepository.findByAppleId(appleOAuthResponse.getSub());
                    break;
                } catch (InvalidKeySpecException e) {

                } catch (IOException e) {

                } catch (NoSuchAlgorithmException e) {

                }
            }
        }
        if (foundUser != null && foundUser.getEmail() != null) {
            System.out.println(foundUser.getEmail());
        }

        if (userRepository.findByNickname(signupRequestDto.getNickname()) != null) {
            throw new DataIntegrityViolationException("이미 존재하는 닉네임입니다.");
        }

        if (socialType.equals("androidGoogle")) {
            socialType = "google";
        } else if (socialType.equals("webApple")) {
            socialType = "apple";
            signupRequestDto.setEmail(foundUser.getEmail().split("apple_")[1]);
        }

        if (foundUser != null) {
            if (foundUser.getIsActive()) {
                throw new DataIntegrityViolationException("이미 존재하는 사용자입니다.");
            } else {
                foundUser.setIsActive(true);
                foundUser.setEmail(socialType + "_" + signupRequestDto.getEmail());
                foundUser.setNickname(signupRequestDto.getNickname());
                setOperations.add("nicknames", signupRequestDto.getNickname());
                foundUser.setEmoji(signupRequestDto.getEmoji());
                Random random = new Random();
                foundUser.setColor(colors.get(random.nextInt(colors.size())));
                foundUser.setRoles("ROLE_USER");
                User savedUser = userRepository.save(foundUser);
                String accessToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
                String refreshToken = refreshTokenProvider.createToken(savedUser.getId(), savedUser.getRoleList());
                redisTemplate.opsForHash().put("refresh", refreshToken, savedUser.getId().toString());
                return new LoginResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getEmoji(), savedUser.getColor(), accessToken, refreshToken, null);
            }
        }

        User newUser = new User();
        newUser.setEmail(socialType + "_" + signupRequestDto.getEmail());
        newUser.setNickname(signupRequestDto.getNickname());
        setOperations.add("nicknames", signupRequestDto.getNickname());
        newUser.setEmoji(signupRequestDto.getEmoji());
        Random random = new Random();
        newUser.setColor(colors.get(random.nextInt(colors.size())));
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
        return new LoginResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getEmoji(), savedUser.getColor(), accessToken, refreshToken, null);
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
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        Set<String> nicknames = setOperations.members("nicknames");
        while (true) {
            Random random = new Random();
            String adjective = adjectives[random.nextInt(adjectives.length)];
            String noun = nouns[random.nextInt(nouns.length)];
            String nickname = adjective + " " + noun;
            NicknameResDto nicknameResDto = new NicknameResDto(nickname);
            if (nicknames == null) {
                return nicknameResDto;
            } else if (!nicknames.contains(nickname)) {
                return nicknameResDto;
            }
        }
    }

    public void deleteUser(String accessToken) {
        // maria db에서 탈퇴 처리
        User user = this.checkUser(accessToken);

        userRepository.delete(user);

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

//    public AppleOAuthResponseDto getAppleUserInfo(String accessToken) throws IOException {
//        RestTemplate restTemplate = new RestTemplateBuilder().build();
//        String userInfoUrl = "https://appleid.apple.com/auth/userinfo";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(accessToken);
//        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
//
//        try {
//            ResponseEntity<AppleOAuthResponseDto> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, httpEntity, AppleOAuthResponseDto.class);
//            return response.getBody();
//        } catch (HttpClientErrorException e) {
//            throw new IllegalArgumentException("Apple User Info Error");
//        }
//    }

    public void deleteAppleUser(String accessToken, String appleCode, String domainType) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        User user = checkUser(accessToken);
        String appleAccessToken = getAppleToken(appleCode, "access", domainType);
//        // 사용자 정보 조회
//        AppleOAuthResponseDto userInfo = getAppleUserInfo(appleAccessToken);
        String clientId;
        if (domainType == "web" || domainType == "webDelete") {
            clientId = appleClientId;
        } else {
            clientId = appleClientAppId;
        }

        // 사용자 삭제 API 호출
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String revokeUrl = "https://appleid.apple.com/auth/revoke";

        try {
            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", generateClientSecret(clientId));
            params.add("token", appleAccessToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, httpEntity, String.class);
            System.out.println("애플응답 : "+ response.getBody());
            userRepository.delete(user);

            // refresh token 삭제
            Set<Object> keys =  redisTemplate.opsForHash().keys("refresh");
            for (Object key : keys) {
                Long userIdx = Long.parseLong((String) redisTemplate.opsForHash().get("refresh", key));
                if (Objects.equals(userIdx, user.getId())) {
                    redisTemplate.opsForHash().delete("refresh", key);
                    break;
                }
            }
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Apple Delete User Error");
        }


    }



    public MyInfoResDto getMyInfo(String accessToken) {
        User user = this.checkUser(accessToken);
        String emoji = userRepository.findEmojiById(user.getId());
        Optional<Message> message = messageRepository.findFirstMessageByUserOrderByCreatedAtDesc(user);
        if (message.isPresent()) {
            Long messageCnt = messageRepository.findTodayCountByUser(user);
            Long fire = expressionRepository.countByMessageAndEmotionName(message.get(), "fire");
            Long tear = expressionRepository.countByMessageAndEmotionName(message.get(), "tear");
            Long thumbsUp = expressionRepository.countByMessageAndEmotionName(message.get(), "thumbsUp");
            Long heart = expressionRepository.countByMessageAndEmotionName(message.get(), "heart");
            return MyInfoResDto.builder()
                    .emoji(emoji)
                    .message(message.get().getContent())
                    .messageCnt(messageCnt)
                    .fire(fire)
                    .tear(tear)
                    .thumbsUp(thumbsUp)
                    .heart(heart)
                    .build();
        }
        return MyInfoResDto.builder()
                .emoji(emoji)
                .message(null)
                .messageCnt(0L)
                .fire(0L)
                .tear(0L)
                .thumbsUp(0L)
                .heart(0L)
                .build();
    }

    public User checkUser(String accessToken) {
        User user = jwtTokenProvider.getUser(accessToken);
        if (user == null || !user.getIsActive()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        return user;
    }

    public LoginResponseDto getAccessToken(String refreshToken) {
        if (redisTemplate.opsForHash().get("refresh", refreshToken) == null) {
            throw new EntityNotFoundException("존재하지 않는 토큰입니다.");
        }
        Long userIdx = Long.parseLong((String) redisTemplate.opsForHash().get("refresh", refreshToken));
        User user = userRepository.findByIdAndIsActiveTrue(userIdx);
        if (user == null) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        if (!refreshTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationServiceException("토큰이 만료된 유저입니다.");
        }

        if (refreshTokenProvider.getTokenValidTime(refreshToken).isBefore(LocalDateTime.now().plusDays(7))) {
            refreshToken = refreshTokenProvider.createToken(user.getId(), user.getRoleList());
            redisTemplate.opsForHash().put("refresh", refreshToken, user.getId().toString());
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getRoleList());
        return LoginResponseDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .color(user.getColor())
                .user_idx(userIdx)
                .nickname(user.getNickname())
                .emoji(user.getEmoji())
                .email(user.getEmail())
                .build();
    }

    public Long checkUserByAccessToken(String accessToken) {
        Long userIdx = jwtTokenProvider.getUserIdx(accessToken);
        Optional<User> user = userRepository.findById(userIdx);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("유저가 존재하지 않습니다.");
        } else if (!user.get().getIsActive()) {
            throw new EntityNotFoundException("삭제된 유저입니다.");
        }
        return userIdx;
    }

    public LoginResponseDto androidGoogleLogin(String idToken) {
        ResponseEntity<?> response = this.checkAndroidGoogleIdToken(idToken);
        AndroidGoogleResponseDto androidGoogleResponseDto = (AndroidGoogleResponseDto) response.getBody();

        User user = userRepository.findByEmail("google_" + androidGoogleResponseDto.getEmail());
        if (user == null) {
            return LoginResponseDto.builder()
                    .user_idx(null)
                    .email(((AndroidGoogleResponseDto) response.getBody()).getEmail())
                    .emoji(null)
                    .color(null)
                    .refreshToken(null)
                    .nickname(null)
                    .accessToken(null)
                    .build();
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getRoleList());
        String refreshToken = refreshTokenProvider.createToken(user.getId(), user.getRoleList());
        redisTemplate.opsForHash().put("refresh", refreshToken, user.getId().toString());
        return LoginResponseDto.builder()
                .user_idx(user.getId())
                .email(androidGoogleResponseDto.getEmail())
                .nickname(user.getNickname())
                .emoji(user.getEmoji())
                .color(user.getColor())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }



    public ResponseEntity<?> checkAndroidGoogleIdToken(String idToken) {
        String GOOGLE_USERINFO_REQUEST_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl(GOOGLE_USERINFO_REQUEST_URL)
                    .build();
            AndroidGoogleResponseDto res = webClient.get()
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> {
                        throw new AuthenticationServiceException("존재하지 않는 idToken입니다.");
                    })
                    .bodyToMono(AndroidGoogleResponseDto.class)
                    .block();
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    public void logout(String accessToken) {
        User user = this.checkUser(accessToken);

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

    public LoginResponseDto getAppleUserInfo(AppleCodeReqDto appleCodeReqDto) {
        try {
            String appleIdToken = this.getAppleToken(appleCodeReqDto.getAppleCode(), "id", "web");
            ResponseEntity<?> response = this.checkAppleToken(appleIdToken);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
            }
            AppleOAuthResponseDto appleOAuthResponse = (AppleOAuthResponseDto) response.getBody();
            User foundUser = userRepository.findByAppleId(appleOAuthResponse.getSub());
            String accessToken = jwtTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
            String refreshToken = refreshTokenProvider.createToken(foundUser.getId(), foundUser.getRoleList());
            redisTemplate.opsForHash().put("refresh", refreshToken, foundUser.getId().toString());
            return LoginResponseDto.builder()
                    .user_idx(foundUser.getId())
                    .nickname(foundUser.getNickname())
                    .email(foundUser.getEmail())
                    .emoji(foundUser.getEmoji())
                    .color(foundUser.getColor())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (InvalidKeySpecException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }

}
