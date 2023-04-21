package com.fourtytwo.dto.feed;

import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.place.PlaceWithTimeResDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaceFeedResDto {

    private List<MessageResDto> messagesInfo;
    private PlaceWithTimeResDto placeWithTimeInfo;

}
