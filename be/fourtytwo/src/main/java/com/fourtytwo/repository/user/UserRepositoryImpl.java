package com.fourtytwo.repository.user;

import com.fourtytwo.entity.QMessage;
import com.fourtytwo.entity.QUser;
import com.fourtytwo.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QUser user = QUser.user;

    @Override
    public String findEmojiById(Long id) {
        return queryFactory
                .select(user.emoji)
                .from(user)
                .where(user.id.eq(id))
                .fetchOne();
    }
}
