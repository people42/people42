package com.fourtytwo.dto.message;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MyMessageHistoryResDto {

    private String content;
    private LocalDateTime createdAt;
    private Long heart;
    private Long fire;
    private Long tear;
    private Long thumbsUp;

}
