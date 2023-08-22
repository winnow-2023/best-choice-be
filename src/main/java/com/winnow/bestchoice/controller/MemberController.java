package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.domain.Member;
import com.winnow.bestchoice.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
//@RequestMapping("/api/v1/users")
public class MemberController {
    private final MemberService memberService;

//    @GetMapping
//    public ResponseEntity<?> getUser() {
//        Member member = memberService.getMember();
//        return ResponseEntity.ok().body(member);
//    }

    @GetMapping("/oauth2/code/{provider}")
    public String callback(@RequestParam("code") String code, @PathVariable String provider) {
      log.info("code : {}, provider : {}", code, provider);
      return provider;
    }

}
