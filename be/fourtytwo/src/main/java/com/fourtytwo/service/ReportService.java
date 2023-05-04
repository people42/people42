package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.report.ReportReqDto;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.Report;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.alert.ReportRepository;
import com.fourtytwo.repository.message.MessageRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReportService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final MessageRepository messageRepository;

    public void reportUser(String accessToken, ReportReqDto reportReqDto) {

        User reportUser = checkUser(accessToken);

        Optional<Message> message = messageRepository.findById(reportReqDto.getMessageIdx());
        if (message.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 메시지입니다.");
        } else if (!message.get().getIsActive()) {
            throw new EntityNotFoundException("삭제된 메시지입니다.");
        }
        User reportedUser = message.get().getUser();
        if (!reportedUser.getIsActive()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        if (reportRepository.findByMessageAndUser1(message.get(), reportUser).isPresent()) {
            throw new DataIntegrityViolationException("이미 신고한 메시지입니다.");
        }

        Report report = Report.builder()
                .user1(reportUser)
                .user2(reportedUser)
                .content(reportReqDto.getContent())
                .message(message.get())
                .build();

        reportRepository.save(report);

        // 신고 3회 누적되면 부적절한 메시지로 분류
        if (reportRepository.countByMessage(message.get()) == 3L) {
            message.get().setIsInappropriate(true);
            messageRepository.save(message.get());
        }

    }

    public User checkUser(String accessToken) {
        User user = jwtTokenProvider.getUser(accessToken);
        if (user == null || !user.getIsActive()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        }
        return user;
    }


}
