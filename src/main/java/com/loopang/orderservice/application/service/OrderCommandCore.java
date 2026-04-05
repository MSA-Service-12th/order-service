package com.loopang.orderservice.application.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.OrderAccess;
import com.loopang.orderservice.domain.vo.HubManager;
import com.loopang.orderservice.domain.vo.OrderItem;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;
import com.loopang.orderservice.domain.vo.UserType;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandCore {

	private final OrderRepository orderRepository;
	private final OrderAccess orderAccess;

	@Transactional
	public Order saveOrder(Supplier supplier, Receiver receiver, OrderItem orderItem) {
		Order order = Order.create(supplier, receiver, orderItem);
		return orderRepository.save(order);
	}

	@Transactional
	public Order approveOrder(UUID orderId, UUID userId, String userName, UUID managedHubId, UserType userType) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 허브 관리자 또는 마스터 관리자 권한 확인
		orderAccess.validateUpdateDeleteAccess(userId, userType, managedHubId, order);

		// 주문 승인 처리 (상태 변경: WAIT_TO_APPROVAL -> ACCEPTED)
		order.acceptBy(HubManager.of(userId, userName));

		return order;
	}

	@Transactional
	public void handleInventoryCheckResult(UUID orderId, Integer balance) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		if (balance < 0) {
			order.cancel();
		} else {
			order.waitToApproval();
		}
	}

	@Transactional
	public Order cancelOrder(UUID orderId, UUID userId, UUID managedHubId, UserType userType) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 허브 관리자 또는 마스터 관리자 권한 확인
		orderAccess.validateUpdateDeleteAccess(userId, userType, managedHubId, order);

		// 주문 취소 처리 (상태 변경: PENDING, WAIT_TO_APPROVAL -> CANCELLED)
		order.cancel();

		return order;
	}

	@Transactional
	public Order deleteOrder(UUID orderId, UUID userId, UUID managedHubId, UserType userType) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		orderAccess.validateUpdateDeleteAccess(userId, userType, managedHubId, order);
		order.delete(userId);

		return order;
	}
}
