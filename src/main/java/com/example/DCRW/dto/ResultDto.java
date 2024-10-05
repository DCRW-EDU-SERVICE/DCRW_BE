package com.example.DCRW.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class ResultDto<T> {
    private HttpStatus status;
    private String message;
    private T data;

    public ResultDto(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static<T> ResultDto<T> res(final HttpStatus status, final String message) {
        return res(status, message, null);
    }

    public static<T> ResultDto<T> res(final HttpStatus statusCode, final String resultMsg, final T t) {
        return ResultDto.<T>builder()
                .data(t)
                .status(statusCode)
                .message(resultMsg)
                .build();
    }

}
