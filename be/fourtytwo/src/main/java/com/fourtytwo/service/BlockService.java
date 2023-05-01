package com.fourtytwo.service;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.block.BlockReqDto;
import com.fourtytwo.entity.Block;
import com.fourtytwo.entity.User;
import com.fourtytwo.repository.block.BlockRepository;
import com.fourtytwo.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public void makeBlock(String accessToken, BlockReqDto blockReqDto) {
        User user = jwtTokenProvider.getUser(accessToken);
        Optional<User> blockedUser = userRepository.findById(blockReqDto.getUserIdx());
        if (user == null || blockedUser.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 유저입니다.");
        } else if (!user.getIsActive() || !blockedUser.get().getIsActive()) {
            throw new EntityNotFoundException("삭제된 유저입니다.");
        } else if (user.getId().equals(blockReqDto.getUserIdx())) {
            throw new DataIntegrityViolationException("요청한 유저와 같은 유저입니다.");
        }

        User bigUser = user.getId() > blockedUser.get().getId() ? user : blockedUser.get();
        User smallUser = user.getId() > blockedUser.get().getId() ? blockedUser.get() : user;

        Block block = Block.builder()
                .user1(smallUser)
                .user2(bigUser)
                .build();
        blockRepository.save(block);
    }

}
