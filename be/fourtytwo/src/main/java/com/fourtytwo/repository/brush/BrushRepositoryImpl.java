package com.fourtytwo.repository.brush;

import com.fourtytwo.entity.Brush;
import com.fourtytwo.entity.QBrush;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BrushRepositoryImpl implements BrushRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BrushRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QBrush brush = QBrush.brush;

    // 유저 idx로 해당 유저의 24시간 이내 모든 스침 조회
    @Override
    public List<Brush> findRecentBrushByUserIdxOrderByTimeDesc(Long userIdx) {

        LocalDateTime now = LocalDateTime.now();

        return queryFactory
                .selectFrom(brush)
                .where(brush.createdAt.after(now.minusDays(1L)).and(brush.user1.id.eq(userIdx).or(brush.user2.id.eq(userIdx))))
                .orderBy(brush.createdAt.desc())
                .fetch();
    }

    // 두 유저의 스침 횟수 조회
    @Override
    public Long findBrushCntByUserIdxs(Long userIdx1, Long userIdx2) {

        Long smallIdx = Math.min(userIdx1, userIdx2);
        Long bigIdx = Math.max(userIdx1, userIdx2);

        return queryFactory
                .select(brush.count())
                .from(brush)
                .where(brush.user1.id.eq(smallIdx).and(brush.user2.id.eq(bigIdx)))
                .groupBy(brush.user1.id, brush.user2.id)
                .fetchOne();
    }

    @Override
    public List<Tuple> findBrushCountByUserIdxAndUserIdxList(Long userIdx, List<Long> userIdxList) {

        List<Long> smallIdxList = new ArrayList<>();
        List<Long> bigIdxList = new ArrayList<>();

        for (Long idx : userIdxList) {
            if (idx < userIdx) {
                smallIdxList.add(idx);
            } else {
                bigIdxList.add(idx);
            }
        }

        return queryFactory
                .select(brush.id, brush.count())
                .from(brush)
                .where(brush.in(
                                JPAExpressions
                                    .select(brush)
                                    .from(brush)
                                    .where(brush.user1.id.eq(userIdx).and(brush.user2.id.in(bigIdxList)))
                                )
                        .or(
                        brush.in(
                                JPAExpressions
                                    .selectFrom(brush)
                                    .where(brush.user2.id.eq(userIdx).and(brush.user1.id.in(smallIdxList)))
                                )
                        )
                )
                .groupBy(brush.id, brush.user1)
                .fetch();
    }

}
