package com.fourtytwo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupRequestDto {

    @NotBlank(message = "닉네임이 비어있습니다.")
    private String nickname;

    @NotBlank(message = "이메일이 비어있습니다.")
    private String email;

    @NotBlank(message = "토큰이 비어있습니다.")
    private String o_auth_token;
}
