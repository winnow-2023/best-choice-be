package com.winnow.bestchoice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MemberViewController {

    @RequestMapping("/")
    public String index() {
        return "login";
    }
}
