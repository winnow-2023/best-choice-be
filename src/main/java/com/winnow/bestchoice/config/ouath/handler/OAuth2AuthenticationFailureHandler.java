package com.winnow.bestchoice.config.ouath.handler;

import com.winnow.bestchoice.config.ouath.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final static String REDIRECT_PATH = "http://localhost:5173/login/callback";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


}
