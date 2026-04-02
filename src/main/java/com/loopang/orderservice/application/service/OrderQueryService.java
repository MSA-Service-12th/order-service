package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.repository.OrderQueryRepository;
import com.loopang.orderservice.domain.vo.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

	private final OrderQueryRepository orderQueryRepository;

	public OrderDetailsDto getOrder(UUID orderId, UUID userId, UserType userType) {
		Order order = orderQueryRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		return OrderDetailsDto.from(order);
	}

	public Page<OrderSummaryDto> searchOrders(OrderSearchConditionDto condition, Pageable pageable, UUID userId, UserType userType) {
		return orderQueryRepository.findAllOrders(condition, pageable)
				.map(OrderSummaryDto::from);
	}
}
