package com.fourtytwo.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResDto {

    private Long messageIdx;
    private String content;
    private Long userIdx;
    private String nickname;
    private String emoji;
    private Integer color;
    private Long brushCnt;

}
