package com.fourtytwo.controller;

import com.fourtytwo.dto.place.GpsReqDto;
import com.fourtytwo.service.GpsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gps")
@AllArgsConstructor
public class BrushController {
    private GpsService gpsService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Object>> renewGps(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                        @RequestBody GpsReqDto gps) {
        gpsService.renewGps(accessToken, gps);
        return ApiResponse.ok(null);
    }
}
