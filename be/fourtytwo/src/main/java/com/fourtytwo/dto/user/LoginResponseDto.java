package com.fourtytwo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private Long user_idx;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
