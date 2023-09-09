package com.winnow.bestchoice.config;

import com.winnow.bestchoice.config.jwt.TokenAuthenticationFilter;
import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.config.ouath.exception.RestAuthenticationEntryPoint;
import com.winnow.bestchoice.config.ouath.handler.OAuth2AuthenticationFailureHandler;
import com.winnow.bestchoice.config.ouath.handler.OAuth2AuthenticationSuccessHandler;
import com.winnow.bestchoice.config.ouath.handler.TokenAccessDeniedHandler;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.config.ouath.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.winnow.bestchoice.repository.RefreshTokenRepository;
import com.winnow.bestchoice.service.CustomUserDetailService;
import com.winnow.bestchoice.service.OAuth2UserCustomService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;


@RequiredArgsConstructor
@Configuration
@Getter
@Setter
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .antMatchers("/h2-console/**") //test용 추후 삭제
                .antMatchers("/img/**", "/css/**", "/js/**", "/resources/**"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .cors()
                    .configurationSource(corsConfigurationSource())
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf().disable()
                    .headers().frameOptions().sameOrigin()
                .and()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .accessDeniedHandler(tokenAccessDeniedHandler)
                .and()
                    .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers("/login/**").permitAll()
                    .antMatchers("/api/**", "/pub/**", "/sub/**", "/chat/**").authenticated()
                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                    .redirectionEndpoint()
                    .baseUri("/oauth2/code/**")
                .and()
                    .userInfoEndpoint()
                    .userService(oAuth2UserCustomService)
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler())
                    .failureHandler(oAuth2AuthenticationFailureHandler());

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /*
     * auth 매니저 설정
     * */
    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /*
     * security 설정 시, 사용할 인코더 설정
     * */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 토큰 필터 설정
     * */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                memberRepository,
                tokenProvider,
                refreshTokenRepository
        );
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler((OAuth2AuthorizationRequestBasedOnCookieRepository) oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Cors 설정
     * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
       corsConfig.setAllowCredentials(true);
       corsConfig.addAllowedOrigin("http://localhost:5173");
       corsConfig.addAllowedOrigin("ws://localhost:5173/ws-stomp");
       corsConfig.addAllowedOrigin("ws://localhost:5173");
       corsConfig.addAllowedHeader("*");
       corsConfig.addAllowedMethod("*");

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", corsConfig);
       return source;
    }

}
