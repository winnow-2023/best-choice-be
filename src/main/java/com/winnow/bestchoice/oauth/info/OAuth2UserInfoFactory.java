package com.winnow.bestchoice.oauth.info;

import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.oauth.info.impl.GoogleOAuth2UserInfo;
import com.winnow.bestchoice.oauth.info.impl.KakaoOAuth2UserInfo;
import com.winnow.bestchoice.oauth.info.impl.NaverOAuth2UserInfo;
import com.winnow.bestchoice.type.Provider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(Provider provider, Map<String, Object> attributes) {
        switch (provider) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new CustomException(ErrorCode.INVALID_PROVIDER);
        }
    }
}
