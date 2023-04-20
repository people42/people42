package com.fourtytwo.auth;

import com.fourtytwo.entity.User;
import com.fourtytwo.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdx) throws UsernameNotFoundException {
        Optional<User> user = authRepository.findById(Long.parseLong(userIdx));
        if (user.isPresent()) {
            return new PrincipalDetails(user.get());
        }
        throw new AuthenticationServiceException("유저를 찾을 수 없습니다.");
    }
}
