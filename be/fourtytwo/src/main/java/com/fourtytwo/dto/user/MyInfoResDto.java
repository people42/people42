package com.fourtytwo.dto.user;

import lombok.*;

@Getter
@Builder
public class MyInfoResDto {

    private String emoji;
    private String message;
    private Long messageCnt;

}
