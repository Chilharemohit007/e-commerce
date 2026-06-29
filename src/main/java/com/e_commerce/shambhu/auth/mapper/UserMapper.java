package com.e_commerce.shambhu.auth.mapper;

import com.e_commerce.shambhu.auth.dto.RegisterRequest;
import com.e_commerce.shambhu.auth.dto.RegisterResponse;
import com.e_commerce.shambhu.auth.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(RegisterRequest request) {

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());

        return user;
    }

    public static RegisterResponse toResponse(User user) {

        RegisterResponse response = new RegisterResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setMobileNumber(user.getMobileNumber());

        return response;
    }

}