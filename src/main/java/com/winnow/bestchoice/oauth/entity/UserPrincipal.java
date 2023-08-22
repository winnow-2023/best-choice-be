package com.winnow.bestchoice.oauth.entity;

import com.winnow.bestchoice.domain.Member;
import com.winnow.bestchoice.type.Provider;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {
    private Long id;
    private final String email;
    private final String nickname;
    private final String socialId;
    private final Provider provider;
    private Map<String, Object> attributes;

    public UserPrincipal(String email, String nickname, String socialId, Provider provider) {
        this.email = email;
        this.nickname = nickname;
        this.socialId = socialId;
        this.provider = provider;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return nickname;
    }

    public static UserPrincipal create(Member member) {
        return new UserPrincipal(member.getEmail(), member.getNickname(), member.getSocialId(), member.getProvider());
    }

    public static UserPrincipal create(Member member, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(member);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }
}
