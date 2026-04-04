package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.vo.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderQueryService {
	OrderDetailsDto getOrder(UUID orderId, UUID userId, UserType userType);
	Page<OrderSummaryDto> searchOrders(OrderSearchConditionDto condition, Pageable pageable, UserType userType);
}
