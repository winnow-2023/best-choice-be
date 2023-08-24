package com.winnow.bestchoice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  SERVER_ERROR("내부 서버 오류가 발생했습니다.", INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("잘못된 요청입니다.", BAD_REQUEST),
  REFRESHTOKEN_NOT_FOUND("해당 리프레시 토큰을 찾을 수 없습니다.", NOT_FOUND),
  USER_NOT_FOUND("해당 회원을 찾을 수 없습니다.", NOT_FOUND),
  POST_NOT_FOUND("해당 게시글을 찾을 수 없습니다.", NOT_FOUND),
  INVALID_PROVIDER("유효 하지 않는 Provider 타입입니다.", CONFLICT),
  OAUTH_PROVIDER_MISS_MATCH("Provider Type이 일치하지 않습니다.", BAD_REQUEST),
  ALREADY_REGISTERED_MEMBER("이미 가입된 회원입니다.", CONFLICT),
  INVALID_TOKEN("토큰이 유효하지 않습니다", UNAUTHORIZED);

  private final String description;
  private final HttpStatus httpStatus;

}