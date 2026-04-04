package com.loopang.orderservice.application.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.repository.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderQueryCore {

	private final OrderQueryRepository orderQueryRepository;

	@Transactional(readOnly = true)
	public Order findById(UUID orderId) {
		return orderQueryRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Page<Order> findAllOrders(OrderSearchConditionDto condition, Pageable pageable) {
		return orderQueryRepository.findAllOrders(condition, pageable);
	}
}
