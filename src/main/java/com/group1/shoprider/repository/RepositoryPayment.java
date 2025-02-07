package com.group1.shoprider.repository;

import com.group1.shoprider.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryPayment extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUserId(String userId);
}
