package com.loopang.orderservice.domain.repository;

import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderQueryRepository {

	Optional<Order> findById(UUID orderId);
	Page<Order> findAll(OrderSearchConditionDto condition, Pageable pageable);
}
