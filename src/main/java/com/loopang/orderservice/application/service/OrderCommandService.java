package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderCreateCommandDto;
import com.loopang.orderservice.application.dto.OrderCreateResultDto;
import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
import com.loopang.orderservice.domain.vo.UserType;

import java.util.UUID;

public interface OrderCommandService {
	OrderCreateResultDto createOrder(OrderCreateCommandDto request, UserType userType);
	OrderDeleteCommandDto deleteOrder(UUID orderId, UUID userId, UserType userType);
	void approveOrder(UUID orderId, UUID userId, UserType userType);
}
