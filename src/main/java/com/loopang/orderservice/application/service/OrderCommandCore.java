package com.loopang.orderservice.application.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.repository.OrderRepository;
import com.loopang.orderservice.domain.service.OrderAccess;
import com.loopang.orderservice.domain.vo.*;
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
	public Order handleDeliveryCreation(UUID orderId, UUID deliveryId, UUID departureId, DeliveryStatus deliveryStatus) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 1. 배송ID 업데이트 (최초 생성 피드백 시)
		if (order.getDeliveryId() == null && deliveryId != null) {
			order.updateDeliveryId(deliveryId);
		}

		// 2. 상태 전이: ACCEPTED -> ON_DELIVERY
		// 조건: 배송 상태가 START_DELIVERY이고, 수신한 출발지ID가 주문의 공급업체 허브ID와 일치할 때
		boolean isStartDelivery = deliveryStatus == DeliveryStatus.START_DELIVERY;
		boolean isSupplierHubMatch = order.getSupplier().getHubId().equals(departureId);

		if (isStartDelivery && isSupplierHubMatch && order.getStatus() == OrderStatus.ACCEPTED) {
			order.startDelivery();
		}

		return order;
	}

	@Transactional
	public Order handleDeliveryCompletion(UUID orderId, UUID destinationId, DeliveryStatus deliveryStatus) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 상태 전이: ON_DELIVERY -> COMPLETED
		// 조건: 배송 상태가 COMPLETED이고, 수신한 목적지ID가 주문의 수령업체ID와 일치할 때
		boolean isCompleted = deliveryStatus == DeliveryStatus.COMPLETED;
		boolean isReceiverMatch = order.getReceiver().getReceiverId().equals(destinationId);

		if (isCompleted && isReceiverMatch && order.getStatus() == OrderStatus.ON_DELIVERY) {
			order.complete();
		}

		return order;
	}

	@Transactional
	public Order handleDeliveryRollback(UUID orderId, DeliveryStatus deliveryStatus, boolean isForce) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		boolean isCancelled = deliveryStatus == DeliveryStatus.CANCELLED;

		// 배송 취소 상태이거나 강제 취소(isForce) 요청인 경우 롤백 수행
		if ((isCancelled || isForce) 
				&& order.getStatus() != OrderStatus.CANCELLED
				&& order.getStatus() != OrderStatus.COMPLETED) {
			order.cancel();
		}
		return order;
	}

	@Transactional
	public Order handleInventoryCheckResult(UUID orderId, Integer balance) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
		boolean isOutOfStock = balance < 0;

		if (isOutOfStock && order.getStatus() != OrderStatus.CANCELLED) {
			order.cancel();
		} else if (!isOutOfStock && order.getStatus() == OrderStatus.PENDING) {
			order.waitToApproval();
		}

		// 이미 승인 완료되어 배송 진행/배송 완료된 주문에 대해서는 별도 처리 없이 통과
		return order;
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
