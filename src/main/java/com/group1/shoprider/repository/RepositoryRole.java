package com.group1.shoprider.repository;

import com.group1.shoprider.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryRole extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
