package com.e_commerce.shambhu.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final T data;
    private final List<String> errors;

    private ApiResponse(Builder<T> builder) {
        this.success = builder.success;
        this.status = builder.status;
        this.message = builder.message;
        this.timestamp = builder.timestamp != null
                ? builder.timestamp
                : LocalDateTime.now();
        this.data = builder.data;
        this.errors = builder.errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {

        private boolean success;
        private int status;
        private String message;
        private LocalDateTime timestamp;
        private T data;
        private List<String> errors;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> status(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> errors(List<String> errors) {
            this.errors = errors;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }
}