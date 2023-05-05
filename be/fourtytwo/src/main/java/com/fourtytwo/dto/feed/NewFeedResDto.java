package com.fourtytwo.dto.feed;

import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class NewFeedResDto {

    private PlaceWithTimeResDto placeWithTimeInfo;
    private RecentUsersInfoResDto recentUsersInfo;


}
