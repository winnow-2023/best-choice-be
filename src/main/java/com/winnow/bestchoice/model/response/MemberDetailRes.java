package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.type.Provider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class MemberDetailRes {

    private long memberId;
    private String email;
    private String nickname;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Provider provider;

    public static MemberDetailRes of(Member member) {
        return MemberDetailRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .createdDate(member.getCreatedDate())
                .modifiedDate(member.getModifiedDate())
                .provider(member.getProvider())
                .build();
    }
}
