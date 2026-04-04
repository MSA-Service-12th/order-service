package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.repository.OrderQueryRepository;
import com.loopang.orderservice.domain.service.DeliveryProvider;
import com.loopang.orderservice.domain.service.OrderAccess;
import com.loopang.orderservice.domain.service.UserProvider;
import com.loopang.orderservice.domain.service.dto.CourierData;
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
	private final OrderDtoMapper orderDtoMapper;
	private final OrderAccess orderAccess;

	private final UserProvider userProvider;
	private final DeliveryProvider deliveryProvider;

	public OrderDetailsDto getOrder(UUID orderId, UUID userId, UserType userType) {
		Order order = orderQueryRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 권한 검증 분기 처리
		if (userType == UserType.DELIVERY) {
			// 현재 주문을 담당할 배송관리자 확인: 해당 배송담당자ID를 조회하여 확인
			UUID assignedCourierId = deliveryProvider.getAssignedCourier(order, userId, userType);
			orderAccess.validateReadAccess(userId, userType, assignedCourierId, order);
		} else {
			// 마스터, 허브 관리자, 업체 담당자는 기존 로직으로 검증
			UUID managedHubId = userProvider.getHubIdIfHubManager(userId, userType);
			orderAccess.validateReadAccess(userId, userType, managedHubId, order);
		}

		return orderDtoMapper.toDetailsDto(order);
	}

	public Page<OrderSummaryDto> searchOrders(OrderSearchConditionDto condition, Pageable pageable, UserType userType) {
		orderAccess.validateListSearchAccess(userType);

		return orderQueryRepository.findAllOrders(condition, pageable)
				.map(orderDtoMapper::toSummaryDto);
	}
}
