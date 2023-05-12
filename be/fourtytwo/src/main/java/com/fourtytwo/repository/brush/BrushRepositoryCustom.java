package com.fourtytwo.repository.brush;

import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.User;
import com.querydsl.core.Tuple;

import java.util.List;

public interface BrushRepositoryCustom {

    List<Brush> findRecentBrushByUserIdxOrderByTimeDesc(Long userIdx);

    Long findBrushCntByUserIdxs(Long userIdx1, Long userIdx2);

    List<Tuple> findBrushCountByUserIdxAndUserIdxList(Long userIdx, List<Long> userIdxList);

}
