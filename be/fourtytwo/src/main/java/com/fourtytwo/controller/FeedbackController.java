package com.fourtytwo.controller;

import com.fourtytwo.dto.feedback.FeedbackReqDto;
import com.fourtytwo.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedback")
@AllArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Object>> createFeedback(@Valid @RequestBody FeedbackReqDto feedbackReqDto) {
        feedbackService.createFeedback(feedbackReqDto);
        return ApiResponse.ok(null);
    }

}
