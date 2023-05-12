package com.fourtytwo.repository.place;

import com.fourtytwo.entity.*;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaceRepositoryImpl implements PlaceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PlaceRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QMessage message = QMessage.message;
    QPlace place = QPlace.place;
    QBrush brush = QBrush.brush;

    @Override
    public Place findByGps(double latInput, double longInput) {
        QPlace place = QPlace.place;

        BooleanExpression latitudeWithinRange = place.latitude.between(latInput - 0.005, latInput + 0.005);
        BooleanExpression longitudeWithinRange = place.longitude.between(longInput - 0.005, longInput + 0.005);

        return queryFactory
                .selectFrom(place)
                .where(latitudeWithinRange.and(longitudeWithinRange))
                .orderBy(Expressions.numberTemplate(Double.class, "abs(latitude - {0}) + abs(longitude - {1})", latInput, longInput).asc())
                .fetchFirst();
    }
}
