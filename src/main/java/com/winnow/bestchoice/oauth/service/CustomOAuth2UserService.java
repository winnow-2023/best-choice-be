package com.winnow.bestchoice.oauth.service;

import com.winnow.bestchoice.domain.Member;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.oauth.entity.UserPrincipal;
import com.winnow.bestchoice.oauth.info.OAuth2UserInfo;
import com.winnow.bestchoice.oauth.info.OAuth2UserInfoFactory;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.type.MemberStatus;
import com.winnow.bestchoice.type.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        Provider provider = Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, user.getAttributes());
        Member savedMember = memberRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (savedMember != null) {
            if (provider != savedMember.getProvider()) {
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_MISS_MATCH);
            }
            updateUser(savedMember, userInfo);
        } else {
            savedMember = createUser(userInfo, provider);
        }

        return UserPrincipal.create(savedMember, user.getAttributes());
    }

    private Member createUser(OAuth2UserInfo userInfo, Provider provider) {
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(
                null,
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getId(),
                MemberStatus.ACTIVE,
                provider,
                now,
                now);

        return memberRepository.saveAndFlush(member);
    }

    private Member updateUser(Member member, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !member.getEmail().equals(userInfo.getEmail())) {
            member.setNickname(userInfo.getName());
        }

        return member;
    }
}
