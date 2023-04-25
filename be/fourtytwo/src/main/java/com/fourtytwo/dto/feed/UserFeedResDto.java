package com.fourtytwo.dto.feed;

import com.fourtytwo.dto.brush.BrushResDto;
import com.fourtytwo.dto.place.PlaceResDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class UserFeedResDto {

    private Integer brushCnt;
    private Long userIdx;
    private String nickname;

    private List<PlaceResDto> placeResDtos;
}
