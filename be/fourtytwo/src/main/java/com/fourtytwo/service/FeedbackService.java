package com.fourtytwo.service;

import com.fourtytwo.dto.feedback.FeedbackReqDto;
import com.fourtytwo.entity.Feedback;
import com.fourtytwo.repository.feedback.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public void createFeedback(FeedbackReqDto feedbackReqDto) {
        feedbackRepository.save(Feedback.builder().content(feedbackReqDto.getContent()).build());
    }

}
