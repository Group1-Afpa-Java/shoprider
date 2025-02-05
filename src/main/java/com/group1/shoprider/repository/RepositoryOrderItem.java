package com.group1.shoprider.repository;

import com.group1.shoprider.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryOrderItem extends JpaRepository<OrderItem, Long> {

}
