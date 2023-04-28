package com.fourtytwo.repository.message;

import com.fourtytwo.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MessageRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QMessage message = QMessage.message;
    QPlace place = QPlace.place;
    QBrush brush = QBrush.brush;
    QExpression expression = QExpression.expression;
    QEmotion emotion = QEmotion.emotion;

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
                                .where(brush.eq(targetBrush)
                                        .and(brush.user1.id.ne(userIdx))
                                        .and(message.isActive.eq(true))
                                        .and(brush.user1.isActive.eq(true)))
                        ).or(message.eq(
                                JPAExpressions
                                        .selectFrom(message)
                                        .join(brush)
                                        .on(message.eq(brush.message2))
                                        .where(brush.eq(targetBrush)
                                                .and(brush.user2.id.ne(userIdx))
                                                .and(message.isActive.eq(true))
                                                .and(brush.user2.isActive.eq(true)))
                                )
                        )
                )
                .fetchOne();
    }

//    @Override
//    public List<MessageResDto> findRecentFeedList(User user) {
//
//        LocalDateTime now = LocalDateTime.now();
//
//        return queryFactory
//                .select(Projections.fields(MessageResDto.class,
//                                message.id, message.content, message.user.id,
//                                message.user.nickname, message.user.emoji, message.user.color))
//                .from(message)
//                .where(message.in(
//                        JPAExpressions
//                                .select(brush.message1)
//                                .from(brush)
//                                .where(brush.createdAt.after(now.minusDays(1L))
//                                        .and(brush.user2.eq(user)))
//                        )
//                        .or(message.in(
//                                JPAExpressions
//                                        .select(brush.message2)
//                                        .from(brush)
//                                        .where(brush.createdAt.after(now.minusDays(1L))
//                                                .and(brush.user1.eq(user))
//                                        )
//                                )
//                        )
//                )
//                .fetch();
//    }

    @Override
    public String findFirstContentByUserOrderByCreatedAtDesc(User user) {
        return queryFactory
                .select(message.content)
                .from(message)
                .where(message.user.eq(user).and(message.createdAt.after(LocalDate.now().atStartOfDay())))
                .orderBy(message.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Long findTodayCountByUser(User user) {
        return queryFactory
                .select(message.count())
                .from(message)
                .where(message.user.eq(user).and(message.createdAt.after(LocalDate.now().atStartOfDay())))
                .fetchOne();
    }

    @Override
    public Message findRecentByUserIdx(Long userIdx) {
      return queryFactory
              .selectFrom(message)
              .where(message.user.id.eq(userIdx))
              .orderBy(message.createdAt.desc())
              .fetchFirst();
    };

    @Override
    public List<Message> findMessagesByUserAndCreatedAt(User user, LocalDate createdAt) {

        LocalDateTime startOfDay = createdAt.atStartOfDay();
        LocalDateTime endOfDay = createdAt.plusDays(1).atStartOfDay();

        return queryFactory
                .selectFrom(message)
                .where(message.user.eq(user)
                        .and(message.createdAt.between(startOfDay, endOfDay))
                        .and(message.isActive.eq(true)))
                .groupBy(message.id)
                .fetch();
    }
}
