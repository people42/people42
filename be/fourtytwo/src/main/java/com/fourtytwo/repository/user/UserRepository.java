package com.fourtytwo.repository.user;

import com.fourtytwo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    User findByEmail(String email);

    User findByEmailAndIsActiveTrue(String email);

    User findByNickname(String nickname);

    User findByIdAndIsActiveTrue(Long id);

    User findByAppleId(String appleId);

    Optional<User> findByFcmToken(String fcmToken);

}
