package com.fourtytwo.controller;

import com.fourtytwo.dto.block.BlockReqDto;
import com.fourtytwo.dto.fcm.FcmTokenReqDto;
import com.fourtytwo.dto.message.MessageDeleteReqDto;
import com.fourtytwo.dto.message.MyMessageHistoryResDto;
import com.fourtytwo.dto.report.ReportReqDto;
import com.fourtytwo.dto.user.*;
import com.fourtytwo.service.*;
import com.google.api.Http;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@AllArgsConstructor
public class AccountController {

    private final UserService userService;
    private final MessageService messageService;
    private final ReportService reportService;
    private final BlockService blockService;
    private final FcmService fcmService;

    @DeleteMapping("/withdrawal")
    public ResponseEntity<ApiResponse<Object>> deleteUser(HttpServletResponse response,
                                                          @RequestHeader("ACCESS-TOKEN") String accessToken) {
        userService.deleteUser(accessToken);
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ApiResponse.ok(null);
    }
    @DeleteMapping("/withdrawal/apple")
    public ResponseEntity<ApiResponse<Object>> deleteAppleUser(HttpServletResponse response,
                                                               @RequestHeader("ACCESS-TOKEN") String accessToken,
                                                               @RequestBody AppleCodeReqDto appleCodeReqDto) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        userService.deleteAppleUser(accessToken, appleCodeReqDto.getAppleCode(), "app");
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/withdrawal/apple/web")
    public ResponseEntity<ApiResponse<Object>> deleteAppleWebUser(HttpServletResponse response,
                                                                  @RequestHeader("ACCESS-TOKEN") String accessToken,
                                                                  @RequestBody AppleCodeReqDto appleCodeReqDto) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        userService.deleteAppleUser(accessToken, appleCodeReqDto.getAppleCode(), "webDelete");
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
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

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletResponse response,
                                                      @RequestHeader("ACCESS-TOKEN") String accessToken) {
        userService.logout(accessToken);
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ApiResponse.ok(null);
    }

    @PutMapping("/message")
    public ResponseEntity<ApiResponse<Object>> deleteMessage(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                             @Valid @RequestBody MessageDeleteReqDto messageDeleteReqDto) {
        messageService.deleteMessage(accessToken, messageDeleteReqDto);
        return ApiResponse.ok(null);
    }

    @PutMapping("/emoji")
    public ResponseEntity<ApiResponse<Object>> changeEmoji(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                           @Valid @RequestBody ChangeEmojiReqDto changeEmojiReqDto) {
        messageService.changeEmoji(accessToken, changeEmojiReqDto);
        return ApiResponse.ok(null);
    }

    @PostMapping("/block")
    public ResponseEntity<ApiResponse<Object>> makeBlock(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                           @Valid @RequestBody BlockReqDto blockReqDto) {
        blockService.makeBlock(accessToken, blockReqDto);
        return ApiResponse.ok(null);
    }

    @PostMapping("/fcm_token")
    public ResponseEntity<ApiResponse<Object>> updateFcmToken(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                              @Valid @RequestBody FcmTokenReqDto fcmTokenReqDto) {
        fcmService.updateFcmToken(accessToken, fcmTokenReqDto.getToken());
        return ApiResponse.ok(null);
    }

    @PutMapping("/nickname")
    public ResponseEntity<ApiResponse<Object>> updateNickname(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                              @Valid @RequestBody NicknameReqDto nicknameReqDto) {
        userService.updateNickname(accessToken, nicknameReqDto);
        return ApiResponse.ok(null);
    }

}
