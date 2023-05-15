package com.fourtytwo.controller;

import com.fourtytwo.dto.place.GpsReqDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import com.fourtytwo.service.GpsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/background")
@AllArgsConstructor
public class BrushController {
    private GpsService gpsService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<PlaceWithTimeResDto>> renewGps(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                                     @RequestBody GpsReqDto gps) {
        return ApiResponse.ok(gpsService.renewGps(accessToken, gps));
    }
}
