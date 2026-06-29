package com.e_commerce.shambhu.auth.service.impl;

import com.e_commerce.shambhu.auth.dto.RegisterRequest;
import com.e_commerce.shambhu.auth.dto.RegisterResponse;
import com.e_commerce.shambhu.auth.entity.Role;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.enums.RoleType;
import com.e_commerce.shambhu.auth.mapper.UserMapper;
import com.e_commerce.shambhu.auth.repo.RoleRepository;
import com.e_commerce.shambhu.auth.repo.UserRepository;
import com.e_commerce.shambhu.auth.service.AuthService;
import com.e_commerce.shambhu.common.exception.BadRequestException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        validateDuplicateUser(request);

        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role",
                                "name",
                                RoleType.ROLE_CUSTOMER.name()
                        ));

        User user = UserMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRoles(Set.of(customerRole));

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    /**
     * Validate duplicate email and mobile number.
     */
    private void validateDuplicateUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email already registered : " + request.getEmail()
            );
        }

        if (request.getMobileNumber() != null
                && !request.getMobileNumber().isBlank()
                && userRepository.existsByMobileNumber(request.getMobileNumber())) {

            throw new BadRequestException(
                    "Mobile number already registered : "
                            + request.getMobileNumber()
            );
        }
    }
}