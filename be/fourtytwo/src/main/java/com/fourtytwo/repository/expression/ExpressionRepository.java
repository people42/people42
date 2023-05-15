package com.fourtytwo.repository.expression;

import com.fourtytwo.entity.Expression;
import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpressionRepository extends JpaRepository<Expression, Long> {

    Optional<Expression> findByMessageAndUserId(Message message, Long userId);

    Long countByMessageAndEmotionName(Message message, String emotionName);

    Long countByMessageUserAndIsReadIsFalseAndMessage_IsInappropriateIsFalseAndMessage_IsActiveIsTrue(User user);

    List<Expression> findByMessageUserAndIsReadIsFalseAndMessage_IsInappropriateIsFalseAndMessage_IsActiveIsTrue(User user);

}
