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
  MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다.", NOT_FOUND),
  POST_NOT_FOUND("해당 게시글을 찾을 수 없습니다.", NOT_FOUND),
  COMMENT_NOT_FOUND("해당 댓글을 찾을 수 없습니다.", NOT_FOUND),
  INVALID_PROVIDER("유효 하지 않는 Provider 타입입니다.", CONFLICT),
  OAUTH_PROVIDER_MISS_MATCH("Provider Type이 일치하지 않습니다.", BAD_REQUEST),
  ALREADY_REGISTERED_MEMBER("이미 가입된 회원입니다.", CONFLICT),
  INVALID_TOKEN("토큰이 유효하지 않습니다", UNAUTHORIZED),
  ALREADY_REGISTERED_NICKNAME("이미 등록된 닉네임 입니다.", CONFLICT),
  POST_MEMBER_ID_MISS_MATCH("글을 작성한 회원이 아닙니다..", UNAUTHORIZED),
  ALREADY_DELETED_POST("이미 삭제된 게시글입니다.", NOT_FOUND),
  CHATROOM_NOT_FOUND("해당 채팅방을 찾을 수 없습니다.", NOT_FOUND),
  CHATROOM_CAPACITY_EXCEEDED("채팅방 입장 가능한 정원이 초과하였습니다.", BAD_REQUEST);

  private final String description;
  private final HttpStatus httpStatus;

}