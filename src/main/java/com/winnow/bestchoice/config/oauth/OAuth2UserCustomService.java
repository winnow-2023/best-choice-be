package com.winnow.bestchoice.config.oauth;

import com.winnow.bestchoice.domain.Member;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.type.AuthProvider;
import com.winnow.bestchoice.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    // 요청을 바탕으로 회원 정보를 담은 객체(OAuth2User) 반환
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    // 회원이 있으면 업데이트, 없으면 회원 생성
    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("nickname");

        Member member = memberRepository.findByEmail(email)
                .map(entity -> entity.changeNickname(nickname))
                .orElse(Member.builder()
                        .nickname(nickname)
                        .email(email)
                        .status(MemberStatus.ACTIVE)
                        .provider(AuthProvider.GOOGLE)
                        .build());
        return memberRepository.save(member);

    }
}
