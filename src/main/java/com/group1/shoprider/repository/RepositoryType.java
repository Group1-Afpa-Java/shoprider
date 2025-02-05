package com.group1.shoprider.repository;

import com.group1.shoprider.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryType extends JpaRepository<Type, Long> {
    Optional<Type> findByName(String name);
}
