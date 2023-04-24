package com.fourtytwo.repository.message;

import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {
}
