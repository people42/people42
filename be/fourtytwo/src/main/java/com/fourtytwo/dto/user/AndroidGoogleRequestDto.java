package com.fourtytwo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AndroidGoogleRequestDto {

    @NotBlank
    private String o_auth_token;

}
