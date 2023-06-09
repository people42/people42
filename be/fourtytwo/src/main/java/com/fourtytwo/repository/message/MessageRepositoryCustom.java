package com.fourtytwo.repository.message;

import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MessageRepositoryCustom {

    Message findByBrushAndUserIdx(Brush brush, Long userIdx);

//    List<MessageResDto> findRecentFeedList(User user);

    Optional<Message> findFirstMessageByUserOrderByCreatedAtDesc(User user);

    Long findTodayCountByUser(User user);

    Message findRecentByUserIdx(Long userIdx);

    List<Message> findMessagesByUserAndCreatedAt(User user, LocalDate createdAt);
}
