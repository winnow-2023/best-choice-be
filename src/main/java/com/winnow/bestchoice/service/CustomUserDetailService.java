package com.winnow.bestchoice.service;


import com.winnow.bestchoice.config.ouath.entity.UserPrincipal;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member user = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return UserPrincipal.create(user);
    }
}
