package com.fourtytwo.controller;

import com.fourtytwo.service.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tmp")
@AllArgsConstructor
public class Tmp {

    private final FcmService fcmService;

    @GetMapping("")
    public void tmp(@RequestParam("token") String token) {
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        fcmService.sendByTokenList(tokens);
    }

}
