package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.service.DeliveryProvider;
import com.loopang.orderservice.domain.service.OrderAccess;
import com.loopang.orderservice.domain.service.UserProvider;
import com.loopang.orderservice.domain.vo.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderQueryFacade implements OrderQueryService {

	private final OrderQueryCore orderQueryCore;
	private final OrderDtoMapper orderDtoMapper;
	private final OrderAccess orderAccess;

	private final UserProvider userProvider;
	private final DeliveryProvider deliveryProvider;

	@Override
	public OrderDetailsDto getOrder(UUID orderId, UUID userId, UserType userType) {
		// 1. 주문 정보 조회 (DB)
		Order order = orderQueryCore.findById(orderId);

		// 2. 인가 정보 확보 (원격 조회 - 트랜잭션 외부)
		UUID correlationId = null;
		switch (userType) {
			case UserType.HUB -> correlationId = userProvider.getHubIdIfHubManager(userId, userType);
			case UserType.DELIVERY -> correlationId = deliveryProvider.getAssignedCourier(order, userId, userType);
		}

		// 3. 권한 검증
		orderAccess.validateReadAccess(userId, userType, correlationId, order);

		// 4. DTO 변환
		return orderDtoMapper.toDetailsDto(order);
	}

	@Override
	public Page<OrderSummaryDto> searchOrders(OrderSearchConditionDto condition, Pageable pageable, UserType userType) {
		// 목록 검색 권한 체크
		orderAccess.validateListSearchAccess(userType);

		// 실제 조회 위임 및 변환
		return orderQueryCore.findAllOrders(condition, pageable)
				.map(orderDtoMapper::toSummaryDto);
	}
}
