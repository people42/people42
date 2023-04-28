package com.fourtytwo.controller;

import com.fourtytwo.dto.message.MyMessageHistoryResDto;
import com.fourtytwo.dto.report.ReportReqDto;
import com.fourtytwo.dto.user.MessageReqDto;
import com.fourtytwo.dto.user.MyInfoResDto;
import com.fourtytwo.service.MessageService;
import com.fourtytwo.service.ReportService;
import com.fourtytwo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@AllArgsConstructor
public class
AccountController {

    private final UserService userService;
    private final MessageService messageService;
    private final ReportService reportService;

    @PutMapping("/withdrawal")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        userService.deleteUser(accessToken);
        return ApiResponse.ok(null);
    }

    @GetMapping("/myinfo")
    public ResponseEntity<ApiResponse<MyInfoResDto>> getMyInfo(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        MyInfoResDto myInfoResDto = userService.getMyInfo(accessToken);
        return ApiResponse.ok(myInfoResDto);
    }

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<Object>> createMessage(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                             @Valid @RequestBody MessageReqDto messageReqDto) {
        String message = messageReqDto.getMessage();
        messageService.createMessage(accessToken, message);
        return ApiResponse.ok(null);
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<Object>> reportUser(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                         @Valid @RequestBody ReportReqDto reportReqDto) {

        reportService.reportUser(accessToken, reportReqDto);
        return ApiResponse.ok(null);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<MyMessageHistoryResDto>>> getMyMessageHistory(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                                                         @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        List<MyMessageHistoryResDto> myMessageHistoryResDtos = messageService.getMyMessageHistoryByDate(accessToken, date);
        return ApiResponse.ok(myMessageHistoryResDtos);
    }

}
