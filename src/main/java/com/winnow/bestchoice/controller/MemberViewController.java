package com.winnow.bestchoice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberViewController {

    @GetMapping("/oauth2/authorization")
    public String login() {
        return "login";
    }
}
