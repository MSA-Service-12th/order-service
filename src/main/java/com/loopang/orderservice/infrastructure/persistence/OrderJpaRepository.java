package com.loopang.orderservice.infrastructure.persistence;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends OrderRepository, JpaRepository<Order, UUID> {
}
