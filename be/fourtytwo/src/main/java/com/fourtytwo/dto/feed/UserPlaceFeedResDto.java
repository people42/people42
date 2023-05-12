package com.fourtytwo.dto.feed;

import com.fourtytwo.dto.message.MessageResDto;
import com.fourtytwo.dto.message.UserMessageResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UserPlaceFeedResDto {
    private List<UserMessageResDto> messagesInfo;
    private Integer brushCnt;

}
