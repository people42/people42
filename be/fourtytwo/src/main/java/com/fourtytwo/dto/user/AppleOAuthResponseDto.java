package com.fourtytwo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppleOAuthResponseDto {

    private String iss;
    private String sub;
    private String aud;
    private long iat;
    private long exp;
    private String nonce;
    private String c_hash;
    private String email;
    private String email_verified;
    private long auth_time;
}
