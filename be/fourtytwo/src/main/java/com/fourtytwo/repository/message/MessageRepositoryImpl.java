package com.fourtytwo.repository.message;

import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MessageRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QMessage message = QMessage.message;
    QPlace place = QPlace.place;
    QBrush brush = QBrush.brush;

    // 유저 idx에 해당하는 유저가 스쳤을 때 상대 메시지 조회
    @Override
    public Message findByBrushAndUserIdx(Brush targetBrush, Long userIdx) {
        return queryFactory
                .selectFrom(message)
                .where(message.eq(
                        JPAExpressions
                                .selectFrom(message)
                                .join(brush)
                                .on(message.eq(brush.message1))
                                .where(brush.eq(targetBrush).and(brush.user1.id.ne(userIdx)))
                        ).or(message.eq(
                                JPAExpressions
                                        .selectFrom(message)
                                        .join(brush)
                                        .on(message.eq(brush.message2))
                                        .where(brush.eq(targetBrush).and(brush.user2.id.ne(userIdx)))
                                )
                        )
                )
                .fetchOne();
    }

    @Override
    public List<MessageResDto> findRecentFeedList(User user) {

        LocalDateTime now = LocalDateTime.now();

        return queryFactory
                .select(Projections.fields(MessageResDto.class,
                                message.id, message.content, message.user.id,
                                message.user.nickname, message.user.emoji, message.user.color))
                .from(message)
                .where(message.in(
                        JPAExpressions
                                .select(brush.message1)
                                .from(brush)
                                .where(brush.createdAt.after(now.minusDays(1L))
                                        .and(brush.user2.eq(user)))
                        )
                        .or(message.in(
                                JPAExpressions
                                        .select(brush.message2)
                                        .from(brush)
                                        .where(brush.createdAt.after(now.minusDays(1L))
                                                .and(brush.user1.eq(user))
                                        )
                                )
                        )
                )
                .fetch();
    }
}
