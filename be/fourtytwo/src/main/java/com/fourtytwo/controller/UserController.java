package com.fourtytwo.controller;

import com.fourtytwo.dto.user.*;
import com.fourtytwo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/check/google")
    public ResponseEntity<ApiResponse<LoginResponseDto>> googleLogin(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.googleLogin(loginRequestDto.getO_auth_token());
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping(path = "/check/apple")
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleLogin(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.appleLogin(loginRequestDto.getO_auth_token());
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
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleWebLogin(@RequestBody MultiValueMap<String, String> requestBody) {
        System.out.println("requestBody.toString(): " + requestBody.toString());
        System.out.println("requestBody.get('id_token'): " + requestBody.get("id_token"));
        System.out.println("requestBody.get('code'): " + requestBody.get("code"));
//        LoginResponseDto loginResponseDto = userService.appleLogin(requestBody.get("id_token").get(0));
//        ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>("OK", 200, loginResponseDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:5174/signin/apple?apple_code=" + requestBody.get("code")));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);

    }


    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<NicknameResDto>> createNickname() {
        NicknameResDto nicknameResDto = userService.createNickname();
        return ApiResponse.ok(nicknameResDto);
    }

    @PostMapping("/signup/google")
    public ResponseEntity<ApiResponse<LoginResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        LoginResponseDto loginResponseDto = userService.signup(signupRequestDto, "google");
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/signup/apple")
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleSignup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        LoginResponseDto loginResponseDto = userService.signup(signupRequestDto, "apple");
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
}
