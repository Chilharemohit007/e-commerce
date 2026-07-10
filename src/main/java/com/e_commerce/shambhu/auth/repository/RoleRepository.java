package com.e_commerce.shambhu.auth.repository;

import com.e_commerce.shambhu.auth.entity.Role;
import com.e_commerce.shambhu.auth.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
