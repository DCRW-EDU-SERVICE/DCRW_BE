package com.example.DCRW.exception;

import com.example.DCRW.dto.ResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .message("유효성 검사 오류")
                .data(errors)
                .build();

//        fieldErrors.forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );

        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }

    // 그 외 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto<String>> handleGenericException(Exception ex) {
        ResultDto resultDto = ResultDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(resultDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
