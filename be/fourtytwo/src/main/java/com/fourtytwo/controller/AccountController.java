package com.fourtytwo.controller;

import com.fourtytwo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@AllArgsConstructor
public class AccountController {

    private final UserService userService;

    @PutMapping("/withdrawal")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        userService.deleteUser(accessToken);
        return ApiResponse.ok(null);
    }

}
