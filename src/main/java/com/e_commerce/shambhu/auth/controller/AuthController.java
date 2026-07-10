package com.e_commerce.shambhu.auth.controller;

import com.e_commerce.shambhu.auth.dto.*;
import com.e_commerce.shambhu.auth.service.AuthService;
import com.e_commerce.shambhu.auth.service.EmailVerificationService;
import com.e_commerce.shambhu.common.response.ApiResponse;
import com.e_commerce.shambhu.common.response.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response =
                authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.created(
                        "User registered successfully",
                        response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        LoginResponse response =
                authService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(
                ResponseBuilder.buildSuccess(
                        "Access token generated successfully.",
                        HttpStatus.OK,
                        response
                )
        );
    }

    @GetMapping("/verify-email")
    public ApiResponse<Object> verifyEmail(
            @RequestParam String token) {

        emailVerificationService.verifyEmail(token);

        return ResponseBuilder.buildSuccess(
                "Email verified successfully.",
                HttpStatus.OK,
                null
        );
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Object> resendVerificationEmail(
            @Valid @RequestBody
            ResendVerificationEmailRequest request) {

        emailVerificationService
                .resendVerificationEmail(request.getEmail());

        return ResponseBuilder.buildSuccess(
                "Email verified successfully.",
                HttpStatus.OK,
                null
        );
    }
}
