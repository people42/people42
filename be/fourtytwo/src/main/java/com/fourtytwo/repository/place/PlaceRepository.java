package com.fourtytwo.repository.place;

import com.fourtytwo.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {
    Place findPlaceById(Long placeIdx);
}
