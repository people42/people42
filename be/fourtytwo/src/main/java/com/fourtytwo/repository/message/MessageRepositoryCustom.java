package com.fourtytwo.repository.message;

import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;

import java.util.List;
import java.util.Optional;

public interface MessageRepositoryCustom {

    Message findByBrushAndUserIdx(Brush brush, Long userIdx);

//    List<MessageResDto> findRecentFeedList(User user);

    String findFirstContentByUserOrderByCreatedAtDesc(User user);

    Long findTodayCountByUser(User user);
}
