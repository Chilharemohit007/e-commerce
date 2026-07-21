package com.e_commerce.shambhu.auth.service;

import com.e_commerce.shambhu.auth.dto.LoginRequest;
import com.e_commerce.shambhu.auth.dto.LoginResponse;
import com.e_commerce.shambhu.auth.dto.RegisterRequest;
import com.e_commerce.shambhu.auth.dto.RegisterResponse;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.security.CustomUserDetails;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refreshAccessToken(String refreshToken);

    User getAuthenticatedUser();

    CustomUserDetails getAuthenticatedUserDetails();
}
