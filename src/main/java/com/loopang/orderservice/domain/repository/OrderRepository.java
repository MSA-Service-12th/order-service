package com.loopang.orderservice.domain.query;

import com.loopang.orderservice.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

	Optional<Order> findById(UUID orderId);
	Page<Order> findAll(Pageable pageable);
}
