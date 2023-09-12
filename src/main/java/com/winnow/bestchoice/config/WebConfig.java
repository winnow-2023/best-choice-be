package com.winnow.bestchoice.config;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.config.resolver.AuthenticationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationResolver());
    }

    @Bean
    public AuthenticationResolver authenticationResolver() {
        return new AuthenticationResolver(tokenProvider);
    }
}
