package com.fourtytwo.repository.place;

import com.fourtytwo.entity.Place;

import java.util.List;

public interface PlaceRepositoryCustom {
    Place findByGps(double latitude, double longitude);
}
