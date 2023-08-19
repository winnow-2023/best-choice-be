package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.service.OAuthService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {


    private final OAuthService oAuthService;

    @RequestMapping("/oauth2/code/{provider}")
    public ResponseEntity<?> callback(
            @PathVariable(name = "provider") String provider,
            @RequestParam(name = "code") String code) throws IOException {

        log.info("응답코드: {}", code);
        log.info("제공자: {}", provider);


        return oAuthService.getGoogleAccessToken(code);
    }




}
