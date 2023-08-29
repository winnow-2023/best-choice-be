package com.winnow.bestchoice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String index() {
        return "환영합니다 베스트초이스 백엔드 서버입니다.";
    }
}
