package com.winnow.bestchoice.dto;

import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GoogleOauthResponseDto {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;


}
