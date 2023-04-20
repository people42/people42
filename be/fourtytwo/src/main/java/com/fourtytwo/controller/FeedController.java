package com.fourtytwo.controller;

import com.fourtytwo.auth.JwtTokenProvider;
import com.fourtytwo.dto.feed.RecentFeedResDto;
import com.fourtytwo.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@AllArgsConstructor
public class FeedController {

    private FeedService feedService;

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<RecentFeedResDto>>> getRecendFeed(@RequestHeader("ACCESS-TOKEN") String accessToken) {
        List<RecentFeedResDto> recentFeedResDtos = feedService.findRecentBrush(accessToken);
        return ApiResponse.ok(recentFeedResDtos);
    }

}
