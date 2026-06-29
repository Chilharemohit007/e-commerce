package com.e_commerce.shambhu.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNumber;

    // getters & setters
}
