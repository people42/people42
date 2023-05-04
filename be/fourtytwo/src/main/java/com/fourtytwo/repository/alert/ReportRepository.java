package com.fourtytwo.repository.alert;

import com.fourtytwo.entity.Message;
import com.fourtytwo.entity.Report;
import com.fourtytwo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

    Optional<Report> findByMessageAndUser1(Message message, User user1);

    Long countByMessage(Message message);

}
