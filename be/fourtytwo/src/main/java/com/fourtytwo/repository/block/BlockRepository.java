package com.fourtytwo.repository.block;

import com.fourtytwo.entity.Block;
import com.fourtytwo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

}
