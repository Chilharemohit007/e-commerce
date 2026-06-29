package com.e_commerce.shambhu.auth.dto;

import java.util.List;

public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private Long userId;

    private String email;

    private List<String> roles;

    public LoginResponse() {
    }

    // getters & setters
}
