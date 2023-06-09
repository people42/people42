package com.fourtytwo.repository.brush;

import com.fourtytwo.entity.Brush;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BrushRepository extends JpaRepository<Brush, Long>, BrushRepositoryCustom {

    List<Brush> findBrushesByUser1IdAndUser2IdAndPlaceIdAndMessage1_IsActiveTrueAndMessage2_IsActiveTrueOrderByCreatedAtDesc(Long user1Idx, Long user2Idx, Long placeIdx);

    Long countByUser1IdAndUser2IdAndCreatedAtIsBefore(Long user1Idx, Long user2Idx, LocalDateTime time);

    List<Brush> findBrushesByUser1IdAndUser2IdAndUser1_IsActiveTrueAndUser2_IsActiveTrueAndMessage1_IsActiveTrueAndMessage2_IsActiveTrueOrderByCreatedAtDesc(Long user1Idx, Long user2Idx);
}
