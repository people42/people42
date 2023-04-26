package com.fourtytwo.repository.place;

import com.fourtytwo.entity.Place;
import com.fourtytwo.entity.QBrush;
import com.fourtytwo.entity.QMessage;
import com.fourtytwo.entity.QPlace;
import com.querydsl.core.types.dsl.BooleanExpression;
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
                .fetchFirst();
    }
}
