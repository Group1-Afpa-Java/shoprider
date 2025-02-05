package com.group1.shoprider.repository;

import com.group1.shoprider.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RepositoryOrder extends JpaRepository<Order, Long> {

    Optional<Order> findByUser_Username(String username);

}
