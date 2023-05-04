package com.fourtytwo.controller;

import com.fourtytwo.dto.alert.AlertCntResDto;
import com.fourtytwo.service.FcmService;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alert")
@AllArgsConstructor
public class AlertController {

    private final FcmService fcmService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<AlertCntResDto>> getMyAlertCnt(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        AlertCntResDto alertCntResDto = fcmService.getMyAlertCnt(accessToken);
        return ApiResponse.ok(alertCntResDto);
    }

}
