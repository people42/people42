package com.fourtytwo.dto.feed;

import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import lombok.Data;

@Data
public class RecentFeedResDto {

    private MessageResDto recentMessageInfo;
    private PlaceWithTimeResDto placeWithTimeInfo;

}
