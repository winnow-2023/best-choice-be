package com.winnow.bestchoice.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  SERVER_ERROR("내부 서버 오류가 발생했습니다.", INTERNAL_SERVER_ERROR),
  INVALID_REQUEST("잘못된 요청입니다.", BAD_REQUEST)
  ;

  private final String description;
  private final HttpStatus httpStatus;

}