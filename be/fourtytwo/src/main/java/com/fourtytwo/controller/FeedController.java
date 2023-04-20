package com.fourtytwo.controller;

import com.fourtytwo.dto.feed.RecentFeedResDto;
import com.fourtytwo.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@AllArgsConstructor
public class FeedController {

    private FeedService feedService;

    @GetMapping("/recent/{userIdx}")
    public ResponseEntity<List<RecentFeedResDto>> getRecendFeed(@PathVariable Long userIdx) {
        List<RecentFeedResDto> recentFeedResDtos = feedService.findRecentBrush(userIdx);
        System.out.println(recentFeedResDtos);
        return new ResponseEntity<>(recentFeedResDtos, HttpStatus.OK);

    }

}
