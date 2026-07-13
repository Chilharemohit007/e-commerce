package com.e_commerce.shambhu.common.exception;

import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ApiResponse<Object> response = ResponseBuilder.buildError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                List.of(ex.getMessage())
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + " : " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(ResponseBuilder.badRequest(
                        "Validation Failed",
                        errors).getBody());
    }
}
