package com.fourtytwo.repository.emotion;

import com.fourtytwo.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
}
