package com.winnow.bestchoice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }



}
