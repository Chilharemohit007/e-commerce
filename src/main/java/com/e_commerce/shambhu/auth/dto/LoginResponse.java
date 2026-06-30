package com.e_commerce.shambhu.auth.dto;

import java.util.List;

public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private Long userId;

    private String email;

    private List<String> roles;

}