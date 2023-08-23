package com.winnow.bestchoice.service;


import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getMemberId();
        Member user = userService.findById(userId);
        return tokenProvider.generateToken(user, Duration.ofDays(1));
    }
}
