package com.fourtytwo.dto.brush;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class BrushResDto {

    private Long messageIdx;
    private String content;
    private Long placeIdx;
    private String placeName;
    private LocalDateTime time;
}
