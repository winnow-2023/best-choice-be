package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class MemberRes {

    private long memberId;
    private String nickname;

    public static MemberRes of(Member member) {
        return new MemberRes(member.getId(), member.getNickname());
    }
}
