package com.winnow.bestchoice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;

import static com.winnow.bestchoice.exception.ErrorCode.INVALID_REQUEST;
import static com.winnow.bestchoice.exception.ErrorCode.SERVER_ERROR;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<?> handleCustomException(CustomException e) {
    log.info("{} is occurred.", e.getErrorCode());
    return ResponseEntity.badRequest().body(new ErrorResponse(
        e.getErrorCode(), e.getErrorMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    StringBuilder errorMessage = new StringBuilder();

    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errorMessage.append(fieldError.getDefaultMessage());
    }

    log.info("MethodArgumentNotValidException is occurred.", e);
    return ResponseEntity.badRequest().body(
        new ErrorResponse(INVALID_REQUEST, errorMessage.toString()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    log.info("MethodArgumentTypeMismatchException is occurred.");
    return ResponseEntity.badRequest().body(
            new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    log.info("DataIntegrityViolationException is occurred.", e);
    return ResponseEntity.badRequest().body(
        new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
    log.info("ConstraintViolationException is occurred.");
    return ResponseEntity.badRequest().body(
            new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription()));
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
    log.info("MissingServletRequestPartException is occurred", e);
    return ResponseEntity.badRequest().body(
            new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleException(Exception e) {
    log.error("Exception is occurred.", e);
    return ResponseEntity.internalServerError().body(
        new ErrorResponse(SERVER_ERROR, SERVER_ERROR.getDescription()));
  }

}
