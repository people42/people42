package com.fourtytwo.dto.place;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlaceWithTimeAndGpsResDto {

    private Long placeIdx;
    private String placeName;
    private LocalDateTime time;
    private Double placeLatitude;
    private Double placeLongitude;

}
