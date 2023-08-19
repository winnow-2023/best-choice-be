package com.winnow.bestchoice.exception;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  SERVER_ERROR("내부 서버 오류가 발생했습니다.", INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("잘못된 요청입니다.", BAD_REQUEST),
  MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", NOT_FOUND),
  REDIRECT_URI_NOT_MATCHED("redirect uri가 일치하지 않습니다.", BAD_REQUEST),
  EMAIL_NOT_FOUND_FROM_PROVIDER("OAuth2 제공자로부터 이메일을 찾을 수 없습니다.", NOT_FOUND),
  AUTH_PROVIDER_NOT_MATCHED("OAuth2 제공자가 일치하지 않습니다.", CONFLICT),
  REFRESH_TOKEN_NOT_MATCHES("refresh 토큰이 일치하지 않습니다.", UNAUTHORIZED),
  NO_REFRESH_TOKEN_COOKIE("리프레시 토큰 쿠키를 찾을 수 없습니다.", NOT_FOUND),

  REFRESHTOKEN_NOT_FOUND("해당 리프레시 토큰을 찾을 수 없습니다.", NOT_FOUND),
  REFRESHTOKEN_INVALID("유효하지 않은 리프레시 토큰입니다.", UNAUTHORIZED);

  private final String description;
  private final HttpStatus httpStatus;

}