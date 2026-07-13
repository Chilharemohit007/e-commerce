package com.e_commerce.shambhu.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    /* ===========================
       SUCCESS RESPONSES
       =========================== */

    public static <T> ResponseEntity<ApiResponse<T>> ok(
            String message,
            T data) {

        return buildSuccess(message, HttpStatus.OK, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(
            String message,
            T data) {

        return buildSuccess(message, HttpStatus.CREATED, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> buildSuccess(
            String message,
            HttpStatus status,
            T data) {

        ApiResponse<T> response =
                ApiResponse.<T>builder()
                        .success(true)
                        .status(status.value())
                        .message(message)
                        .data(data)
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }

    /* ===========================
       ERROR RESPONSES
       =========================== */

    public static ResponseEntity<ApiResponse<Object>> badRequest(
            String message,
            List<String> errors) {

        return buildError(
                message,
                HttpStatus.BAD_REQUEST,
                errors);
    }

    public static ResponseEntity<ApiResponse<Object>> notFound(
            String message) {

        return buildError(
                message,
                HttpStatus.NOT_FOUND,
                List.of(message));
    }

    public static ResponseEntity<ApiResponse<Object>> unauthorized(
            String message) {

        return buildError(
                message,
                HttpStatus.UNAUTHORIZED,
                List.of(message));
    }

    public static ResponseEntity<ApiResponse<Object>> forbidden(
            String message) {

        return buildError(
                message,
                HttpStatus.FORBIDDEN,
                List.of(message));
    }

    public static ResponseEntity<ApiResponse<Object>> buildError(
            String message,
            HttpStatus status,
            List<String> errors) {

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .success(false)
                        .status(status.value())
                        .message(message)
                        .errors(errors)
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}