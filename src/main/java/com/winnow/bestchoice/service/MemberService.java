package com.winnow.bestchoice.service;


import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.response.MemberDetailRes;
import com.winnow.bestchoice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 회원정보 등록
    public void save(Member member) {
        Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());

        if (optionalMember.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_MEMBER);
        }

        memberRepository.save(member);
    }

    @Transactional
    // 회원 닉네임 수정
    public void updateNickname(String nickname, long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        // 닉네임 중복 체크
        if (validNickname(nickname)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }

        findMember.setNickname(nickname);
        memberRepository.save(findMember);
    }

    // 닉네임 중복 체크 메서드
    public boolean validNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    public MemberDetailRes getMemberDetail(long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberDetailRes.of(member);
    }
}
