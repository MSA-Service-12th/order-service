package com.loopang.orderservice.application.service;

import com.loopang.orderservice.application.dto.OrderDetailsDto;
import com.loopang.orderservice.application.dto.OrderSearchConditionDto;
import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.repository.OrderQueryRepository;
import com.loopang.orderservice.domain.service.OrderAccess;
import com.loopang.orderservice.domain.service.UserProvider;
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

	public OrderDetailsDto getOrder(UUID orderId, UUID userId, UserType userType, UUID deliveryId) {
		Order order = orderQueryRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 권한 검증 분기 처리
		if (userType == UserType.DELIVERY) {
			// 배송 담당자는 배송 ID 일치 여부와 userId 등으로 검증
			orderAccess.validateReadAccess(userId, userType, deliveryId, order);
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
