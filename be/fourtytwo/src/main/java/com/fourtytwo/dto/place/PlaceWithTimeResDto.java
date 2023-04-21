package com.fourtytwo.dto.place;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlaceWithTimeResDto {

    private Long placeIdx;
    private String placeName;
    private LocalDateTime time;

}
