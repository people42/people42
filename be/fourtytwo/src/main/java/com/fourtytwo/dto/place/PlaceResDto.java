package com.fourtytwo.dto.place;

import com.fourtytwo.entity.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class PlaceResDto {

    private Long placeIdx;
    private String placeName;
    private Double placeLatitude;
    private Double placeLongitude;
    private Integer brushCnt;

}
