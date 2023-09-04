package com.winnow.bestchoice.service;


import com.winnow.bestchoice.config.ouath.entity.UserPrincipal;
import com.winnow.bestchoice.config.ouath.info.OAuth2UserInfo;
import com.winnow.bestchoice.config.ouath.info.OAuth2UserInfoFactory;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.type.MemberStatus;
import com.winnow.bestchoice.type.Provider;
import com.winnow.bestchoice.util.NicknameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            return save(userRequest, user);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    private OAuth2User save(OAuth2UserRequest userRequest, OAuth2User user) {
        // Provider : 연동사이트(GOOGLE, NAVER, KAKAO)
        Provider provider = Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, user.getAttributes());

        Member savedMember = memberRepository.findByEmail(userInfo.getEmail()).orElse(null);

        // 해당 이메일로 가입한 회원이 있는 경우
        if (savedMember != null) {
            if (provider != savedMember.getProvider()) {
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_MISS_MATCH);
            }
        } else { // 해당 이메일로 가입한 적이 없는 경우
            savedMember = createUser(userInfo, provider);
        }

        return UserPrincipal.create(savedMember, user.getAttributes());
    }

    private Member createUser(OAuth2UserInfo userInfo, Provider provider) {
        LocalDateTime now = LocalDateTime.now();
        log.info("email : {}", userInfo.getEmail());

        Member member = new Member(
                null,
                NicknameUtil.generateNickname(), // 랜덤으로 생성한 닉네임이 들어감
                userInfo.getEmail(),
                userInfo.getId(),
                MemberStatus.ACTIVE,
                provider,
                now,
                now);
        memberRepository.saveAndFlush(member);

        return member;
    }
}

