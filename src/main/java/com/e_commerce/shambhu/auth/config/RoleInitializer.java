package com.e_commerce.shambhu.auth.config;

import com.e_commerce.shambhu.auth.entity.Role;
import com.e_commerce.shambhu.auth.enums.RoleType;
import com.e_commerce.shambhu.auth.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRoleIfNotExists(RoleType.ROLE_ADMIN);

        createRoleIfNotExists(RoleType.ROLE_CUSTOMER);

        createRoleIfNotExists(RoleType.ROLE_SELLER);
    }

    private void createRoleIfNotExists(RoleType roleType) {

        if (roleRepository.findByName(roleType).isEmpty()) {

            Role role = new Role();

            role.setName(roleType);

            roleRepository.save(role);

            System.out.println(roleType + " created.");
        }

    }

}