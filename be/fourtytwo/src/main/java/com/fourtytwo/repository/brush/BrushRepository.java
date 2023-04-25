package com.fourtytwo.repository.brush;

import com.fourtytwo.entity.Brush;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrushRepository extends JpaRepository<Brush, Long>, BrushRepositoryCustom {
    public List<Brush> findBrushesByUser1IdAndUser2Id(Long user1Idx, Long user2Idx);

    public List<Brush> findBrushesByUser1IdAndUser2IdAndPlaceId(Long user1Idx, Long user2Idx, Long placeIdx);
}
