package com.fourtytwo.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FcmTokenReqDto {

    @NotBlank
    private String token;

}
