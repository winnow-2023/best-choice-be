package com.winnow.bestchoice.oauth.handler;

import com.winnow.bestchoice.config.properties.AppProperties;
import com.winnow.bestchoice.domain.Member;
import com.winnow.bestchoice.domain.RefreshToken;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.oauth.info.OAuth2UserInfo;
import com.winnow.bestchoice.oauth.info.OAuth2UserInfoFactory;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.repository.RefreshTokenRepository;
import com.winnow.bestchoice.service.TokenProvider;
import com.winnow.bestchoice.type.Provider;
import com.winnow.bestchoice.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    private final static String REDIRECT_URI_PARAM_COOKIE_NAME = "oauth2_auth_request";


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already benn committed. Unable to redirect to " + targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Provider provider = Provider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        Date now = new Date();
        String accessToken = tokenProvider.generateToken(userInfo.getId(), userInfo.getEmail(), new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()));
        String refreshToken = tokenProvider.generateToken(userInfo.getId(), userInfo.getEmail(), new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpiry()));

        Member findMember = memberRepository.findBySocialId(userInfo.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );


        // DB 저장
        RefreshToken memberRefreshToken = refreshTokenRepository.findByMemberId(findMember.getId()).orElse(null);

        if (memberRefreshToken != null) {
            memberRefreshToken.setRefreshToken(refreshToken);
            refreshTokenRepository.saveAndFlush(memberRefreshToken);
        } else {
            RefreshToken reToken = new RefreshToken(findMember.getId(), refreshToken);
            refreshTokenRepository.saveAndFlush(reToken);
        }

        int cookieMaxAge = (int) appProperties.getAuth().getRefreshTokenExpiry() / 60;

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken, cookieMaxAge);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
//        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
