package com.fourtytwo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginResponseDto {

    private Long user_idx;
    private String email;
    private String nickname;
    private String emoji;
    private String color;
    private String accessToken;
    private String refreshToken;
}
