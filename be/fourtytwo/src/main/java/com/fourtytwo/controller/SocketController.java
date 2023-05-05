package com.fourtytwo.controller;

import com.fourtytwo.dto.message.TotalMessagesCntResDto;
import com.fourtytwo.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/socket")
@AllArgsConstructor
public class SocketController {

    private final MessageService messageService;

    @GetMapping("/total_message_cnt")
    public ResponseEntity<ApiResponse<TotalMessagesCntResDto>> getTotalMessagesCnt() {
        return ApiResponse.ok(messageService.getTotalMessagesCnt());
    }

}
