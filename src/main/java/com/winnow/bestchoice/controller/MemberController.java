package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.annotation.LoginMemberId;
import com.winnow.bestchoice.model.request.UpdateNicknameRequest;
import com.winnow.bestchoice.model.response.CheckNicknameResponse;
import com.winnow.bestchoice.model.response.MemberDetailRes;
import com.winnow.bestchoice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PutMapping
    public ResponseEntity<?> updateNickname(@RequestBody @Valid UpdateNicknameRequest request, @LoginMemberId long memberId) {
        memberService.updateNickname(request.getNickname(), memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nickname-check")
    public ResponseEntity<CheckNicknameResponse> checkNickname(@RequestParam String nickname) {
        Boolean result = memberService.validNickname(nickname);

        return ResponseEntity.ok().body(new CheckNicknameResponse(result));
    }

    @GetMapping("/mypage")
    public ResponseEntity<MemberDetailRes> getMyInfo(@LoginMemberId long memberId) {
        return ResponseEntity.ok(memberService.getMemberDetail(memberId));
    }
    
}
