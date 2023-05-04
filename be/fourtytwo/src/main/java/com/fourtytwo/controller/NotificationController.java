package com.fourtytwo.controller;

import com.fourtytwo.dto.notification.NotificationCntResDto;
import com.fourtytwo.dto.notification.NotificationHistoryResDto;
import com.fourtytwo.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<NotificationCntResDto>> getMyNotificationCnt(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        NotificationCntResDto notificationCntResDto = notificationService.getMyNotificationCnt(accessToken);
        return ApiResponse.ok(notificationCntResDto);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<NotificationHistoryResDto>>> getMyNotificationHistory(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        List<NotificationHistoryResDto> notificationHistoryResDtos = notificationService.getMyNotificationHistory(accessToken);
        return ApiResponse.ok(notificationHistoryResDtos);
    }

}
