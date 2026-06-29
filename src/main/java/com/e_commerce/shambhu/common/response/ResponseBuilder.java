package com.e_commerce.shambhu.common.response;

import org.springframework.http.HttpStatus;

import java.util.List;

public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return buildSuccess(message, HttpStatus.OK, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return buildSuccess(message, HttpStatus.CREATED, data);
    }

    public static <T> ApiResponse<T> buildSuccess(String message,
                                                  HttpStatus status,
                                                  T data) {

        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Object> badRequest(String message,
                                                 List<String> errors) {
        return buildError(message, HttpStatus.BAD_REQUEST, errors);
    }

    public static ApiResponse<Object> notFound(String message) {
        return buildError(
                message,
                HttpStatus.NOT_FOUND,
                List.of(message)
        );
    }

    public static ApiResponse<Object> unauthorized(String message) {
        return buildError(
                message,
                HttpStatus.UNAUTHORIZED,
                List.of(message)
        );
    }

    public static ApiResponse<Object> forbidden(String message) {
        return buildError(
                message,
                HttpStatus.FORBIDDEN,
                List.of(message)
        );
    }

    public static ApiResponse<Object> buildError(String message,
                                                 HttpStatus status,
                                                 List<String> errors) {

        return ApiResponse.builder()
                .success(false)
                .status(status.value())
                .message(message)
                .errors(errors)
                .build();
    }
}