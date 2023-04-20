package com.fourtytwo.repository.brush;

import com.fourtytwo.entity.Brush;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrushRepository extends JpaRepository<Brush, Long>, BrushRepositoryCustom {
}
