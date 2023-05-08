package com.fourtytwo.controller;

import com.fourtytwo.dto.fcm.FcmTokenReqDto;
import com.fourtytwo.dto.user.*;
import com.fourtytwo.service.FcmService;
import com.fourtytwo.service.UserService;
import com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, FcmService fcmService) {
        this.userService = userService;
    }

    @PostMapping("/check/google")
    public ResponseEntity<ApiResponse<LoginResponseDto>> googleLogin(HttpServletResponse response,
                                                                     @Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.googleLogin(loginRequestDto.getO_auth_token());
        if (loginResponseDto.getRefreshToken() != null) {
            setCookie(response, loginResponseDto.getRefreshToken());
        }
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping(path = "/check/apple")
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleLogin(HttpServletResponse response,
                                                                    @Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.appleLogin(loginRequestDto.getO_auth_token());
        if (loginResponseDto.getRefreshToken() != null) {
            setCookie(response, loginResponseDto.getRefreshToken());
        }
        return ApiResponse.ok(loginResponseDto);
    }

//    @PostMapping(path = "/check/apple/web")
//    public ResponseEntity<ApiResponse<LoginResponseDto>> appleWebLogin(@RequestBody AppleCodeReqDto appleCodeReqDto) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
//        System.out.println(appleCodeReqDto);
//        String idToken = userService.getAppleToken(appleCodeReqDto.getAppleCode(), "id", "web");
//        System.out.println(idToken);
//        LoginResponseDto loginResponseDto = userService.appleLogin(idToken);
//        System.out.println(loginResponseDto);
//        return ApiResponse.ok(loginResponseDto);
//    }

    @PostMapping(path = "/check/apple/web", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleWebLogin(HttpServletResponse response,
                                                                       @RequestBody MultiValueMap<String, String> requestBody) {
        LoginResponseDto loginResponseDto = userService.appleLogin(requestBody.get("id_token").get(0));
        HttpHeaders headers = new HttpHeaders();
        if (loginResponseDto.getAccessToken() == null) {
            headers.setLocation(URI.create("https://www.people42.com/signin/apple?apple_code=" + requestBody.get("code").get(0)
                    + "&is_signup=false"));
        } else {
            headers.setLocation(URI.create("https://www.people42.com/signin/apple?apple_code=" + requestBody.get("code").get(0)
                    + "&is_signup=true"));
        }
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<NicknameResDto>> createNickname() {
        NicknameResDto nicknameResDto = userService.createNickname();
        return ApiResponse.ok(nicknameResDto);
    }

    @PostMapping("/signup/google")
    public ResponseEntity<ApiResponse<LoginResponseDto>> signup(HttpServletResponse response,
                                                                @Valid @RequestBody SignupRequestDto signupRequestDto) {
        LoginResponseDto loginResponseDto = userService.signup(signupRequestDto, "google");
        setCookie(response, loginResponseDto.getRefreshToken());
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/signup/apple")
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleSignup(HttpServletResponse response,
                                                                     @Valid @RequestBody SignupRequestDto signupRequestDto) {
        LoginResponseDto loginResponseDto = userService.signup(signupRequestDto, "apple");
        setCookie(response, loginResponseDto.getRefreshToken());
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/signup/apple/web")
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleWebSignup(HttpServletResponse response,
                                                                        @Valid @RequestBody SignupRequestDto signupRequestDto) {
        LoginResponseDto loginResponseDto = userService.signup(signupRequestDto, "webApple");
        setCookie(response, loginResponseDto.getRefreshToken());
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/apple_user_info")
    public ResponseEntity<ApiResponse<LoginResponseDto>> signup(HttpServletResponse response,
                                                                @Valid @RequestBody AppleCodeReqDto appleCodeReqDto) {
        LoginResponseDto loginResponseDto = userService.getAppleUserInfo(appleCodeReqDto);
        setCookie(response, loginResponseDto.getRefreshToken());
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> getAccessToken(@RequestHeader("REFRESH-TOKEN") String refreshToken) {
        LoginResponseDto loginResponseDto = userService.getAccessToken(refreshToken);
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/check/android/google")
    public ResponseEntity<ApiResponse<LoginResponseDto>> androidGoogleLogin(@Valid @RequestBody AndroidGoogleRequestDto androidGoogleRequestDto) {
        LoginResponseDto loginResponseDto = userService.androidGoogleLogin(androidGoogleRequestDto.getO_auth_token());
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/signup/android/google")
    public ResponseEntity<ApiResponse<LoginResponseDto>> androidGoogleSignup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        LoginResponseDto loginResponseDto = userService.signup(signupRequestDto, "androidGoogle");
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/cookie")
    public ResponseEntity<ApiResponse<LoginResponseDto>> getAccessTokenByCookie(@CookieValue(name = "refresh") String refresh) {
        LoginResponseDto loginResponseDto = userService.getAccessToken(refresh);
        return ApiResponse.ok(loginResponseDto);
    }
    
    private void setCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
