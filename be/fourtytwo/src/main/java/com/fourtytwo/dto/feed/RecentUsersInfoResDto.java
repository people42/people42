package com.fourtytwo.dto.feed;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecentUsersInfoResDto {

    private String nickname;
    private int userCnt;
    private List<String> firstTimeUserEmojis;
    private List<String> repeatUserEmojis;

}
