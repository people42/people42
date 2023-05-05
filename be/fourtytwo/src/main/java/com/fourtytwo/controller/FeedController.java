package com.fourtytwo.controller;

import com.fourtytwo.dto.feed.*;
import com.fourtytwo.service.ExpressService;
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
    private ExpressService expressService;

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

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserFeedResDto>> getUserFeeds(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                                    @RequestParam Long userIdx) {
        UserFeedResDto userFeedResDto = feedService.findUserFeeds(accessToken, userIdx);
        return ApiResponse.ok(userFeedResDto);
    }

    @GetMapping("/user/place")
    public ResponseEntity<ApiResponse<UserPlaceFeedResDto>> getUserPlaceFeeds(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                                    @RequestParam Long userIdx,
                                                                    @RequestParam Long placeIdx) {
        UserPlaceFeedResDto userPlaceFeedResDto = feedService.findUserPlaceFeeds(accessToken, userIdx, placeIdx);
        return ApiResponse.ok(userPlaceFeedResDto);
    }

    @PostMapping("/emotion")
    public ResponseEntity<ApiResponse<Object>> express(@RequestHeader("ACCESS-TOKEN") String accessToken,
                                                       @RequestBody ExpressReqDto expressReqDto) {
        expressService.express(accessToken, expressReqDto);
        return ApiResponse.ok(null);
    }

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<NewFeedResDto>>> getNewFeeds(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        List<NewFeedResDto> newFeedResDtos = feedService.findNewFeeds(accessToken);
        return ApiResponse.ok(newFeedResDtos);
    }

}
