package com.e_commerce.shambhu.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT Access Token
     */
    private String accessToken;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * Usually "Bearer"
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access token expiry in milliseconds
     */
    private Long expiresIn;

    /**
     * Authenticated user ID
     */
    private Long userId;

    /**
     * User email
     */
    private String email;

    /**
     * User roles
     */
    private List<String> roles;
}