package com.example.DCRW.exception;

import com.example.DCRW.dto.ResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // valid 유효성 검사 실패 시 발생됨
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultDto<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .data(errors)
                .build();

//        fieldErrors.forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
        ex.printStackTrace();

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResultDto<String>> handleNoSuchElementException(NoSuchElementException ex) {
        ResultDto<String> resultDto = ResultDto.<String>builder()
                .status(HttpStatus.NOT_FOUND)
                .message("요청한 사용자를 찾을 수 없습니다: " + ex.getMessage())
                .build();
        ex.printStackTrace();
        return new ResponseEntity<>(resultDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultDto<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResultDto<String> resultDto = ResultDto.<String>builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("잘못된 요청: " + ex.getMessage())
                .build();
        ex.printStackTrace();

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }

    // 그 외 예외 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultDto<String>> handleRuntimeException(RuntimeException ex) {
        ResultDto<String> resultDto = ResultDto.<String>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("오류 발생: " + ex.getMessage())
                .build();
        ex.printStackTrace();

        return new ResponseEntity<>(resultDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto<String>> handleGenericException(Exception ex) {
        ResultDto<String> resultDto = ResultDto.<String>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("exception 오류 발생: " + ex.getMessage())
                .build();
        ex.printStackTrace();

        return new ResponseEntity<>(resultDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    // 기본 예외 응답 dto (data 없음)
    private ResultDto setDto(HttpStatus httpStatus, String message){
        ResultDto resultDto = ResultDto.builder()
                .status(httpStatus)
                .message(message)
                .build();
        return resultDto;
    }
}
