package com.winnow.bestchoice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorResponse {

  private ErrorCode errorCode;
  private String message;

}
