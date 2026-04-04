package com.loopang.orderservice.domain.repository;

import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderQueryRepository {

	Optional<Order> findById(UUID orderId);
	Page<Order> findAllOrders(OrderSearchConditionDto condition, Pageable pageable,
							  UUID userId, UserType userType, UUID correlationId);
}
