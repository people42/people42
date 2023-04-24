package com.fourtytwo.controller;

import com.fourtytwo.dto.user.*;
import com.fourtytwo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/check/apple")
    public ResponseEntity<ApiResponse<LoginResponseDto>> appleLogin(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.appleLogin(loginRequestDto.getO_auth_token());
        return ApiResponse.ok(loginResponseDto);
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
    public ResponseEntity<ApiResponse<AccessTokenResDto>> getAccessToken(@RequestHeader("REFRESH-TOKEN") String refreshToken) {
        AccessTokenResDto accessTokenResDto = userService.getAccessToken(refreshToken);
        return ApiResponse.ok(accessTokenResDto);
    }
}
