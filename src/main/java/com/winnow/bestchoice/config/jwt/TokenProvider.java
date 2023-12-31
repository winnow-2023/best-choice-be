package com.winnow.bestchoice.config.jwt;


import com.winnow.bestchoice.config.properties.JwtProperties;
import com.winnow.bestchoice.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
    }

    @PostConstruct //for test
    public void createTestJwt() {
        Member member = Member.builder().id(1L).email("test@email.com").build();
        Date now = new Date();
        Duration expiredAt = Duration.ofDays(1);
        String token = makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
        System.out.println("-------------------------");
        System.out.println("test jwt token = " + token);
        System.out.println("-------------------------");
    }

    // JWT 토큰 생성 메서드
    private String makeToken(Date expiry, Member member) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(member.getEmail())
                .claim("id", member.getId())
                .claim("nickname", member.getNickname())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) { // 복호화 과정에서 에러 발생 시 유효하지 않은 토큰
            return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User
                (claims.getSubject(), "", authorities), token, authorities);
    }

    //authentication에서 member pk 한 번에 뽑기
    public Long getMemberId(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        return getMemberId(token);
    }

    // 토큰 기반으로 유저ID를 가져오는 메서드
    public Long getMemberId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    // 토큰 기반으로 회원 닉네임을 가져오는 메서드
    public String getNickname(String token) {
        Claims claims = getClaims(token);
        return claims.get("nickname", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
