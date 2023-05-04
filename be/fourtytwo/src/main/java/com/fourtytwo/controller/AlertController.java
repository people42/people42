package com.fourtytwo.controller;

import com.fourtytwo.dto.alert.AlertCntResDto;
import com.fourtytwo.dto.alert.AlertHistoryResDto;
import com.fourtytwo.service.AlertService;
import com.fourtytwo.service.FcmService;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alert")
@AllArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<AlertCntResDto>> getMyAlertCnt(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        AlertCntResDto alertCntResDto = alertService.getMyAlertCnt(accessToken);
        return ApiResponse.ok(alertCntResDto);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AlertHistoryResDto>>> getMyAlertHistory(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        List<AlertHistoryResDto> alertCntResDtos = alertService.getMyAlertHistory(accessToken);
        return ApiResponse.ok(alertCntResDtos);
    }

}
