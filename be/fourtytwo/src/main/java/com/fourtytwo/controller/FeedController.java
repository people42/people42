package com.fourtytwo.controller;

import com.fourtytwo.dto.feed.PlaceFeedResDto;
import com.fourtytwo.dto.feed.RecentFeedResDto;
import com.fourtytwo.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@AllArgsConstructor
public class FeedController {

    private FeedService feedService;

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<RecentFeedResDto>>> getRecentFeed(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        List<RecentFeedResDto> recentFeedResDtos = feedService.findRecentBrush(accessToken);
        return ApiResponse.ok(recentFeedResDtos);
    }

    @GetMapping("/place")
    public ResponseEntity<ApiResponse<PlaceFeedResDto>> getPlaceFeeds(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                                      @RequestParam Long placeIdx,
                                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time,
                                                                      @RequestParam Integer page,
                                                                      @RequestParam Integer size) {
        PlaceFeedResDto placeFeedResDto = feedService.findPlaceFeeds(accessToken, placeIdx, time, page, size);
        return ApiResponse.ok(placeFeedResDto);
    }

}
