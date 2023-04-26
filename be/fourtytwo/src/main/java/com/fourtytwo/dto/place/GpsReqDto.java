package com.fourtytwo.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GpsReqDto {
    private Double latitude;
    private Double longitude;
}
