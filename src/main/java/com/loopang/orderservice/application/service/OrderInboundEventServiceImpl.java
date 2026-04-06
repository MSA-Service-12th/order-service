package com.loopang.orderservice.application.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.event.OrderEvents;
import com.loopang.orderservice.domain.event.payload.DeliveryUpdatePayload;
import com.loopang.orderservice.domain.event.payload.HubUpdatePayload;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderInboundEventServiceImpl implements OrderInboundEventService {

	private final OrderCommandCore orderCommandCore;
	private final OrderEvents orderEvents;

	@Override
	public void handleInventoryResult(HubUpdatePayload payload) {
		// 허브가 이미 재고 부족 결과를 보낸 상황이므로, 주문 상태만 변경하고 이벤트를 다시 발행하지 않음
		orderCommandCore.handleInventoryCheckResult(payload.orderId(), payload.balance());
	}

	@Override
	public void handleInventoryCheckFailure(HubUpdatePayload payload) {
		// 주문 서비스의 처리 실패(DLT)로 인한 강제 취소 시에는 허브에 알림이 필요함
		Order order = orderCommandCore.handleInventoryCheckResult(payload.orderId(), -1);
		if (order.getStatus() == OrderStatus.CANCELLED) {
			orderEvents.cancelled(order);
		}
	}

	@Override
	public void handleDeliveryCreation(DeliveryUpdatePayload payload) {
		orderCommandCore.handleDeliveryCreation(
				payload.orderId(),
				payload.deliveryId(),
				payload.departureId(),
				payload.deliveryStatus()
		);
	}

	@Override
	public void handleDeliveryCompletion(DeliveryUpdatePayload payload) {
		orderCommandCore.handleDeliveryCompletion(
				payload.orderId(),
				payload.destinationId(),
				payload.deliveryStatus()
		);
	}

	@Override
	public void handleDeliveryRollback(DeliveryUpdatePayload payload, boolean isForce) {
		Order order = orderCommandCore.handleDeliveryRollback(
				payload.orderId(),
				payload.deliveryStatus(),
				isForce
		);

		// 배송 오류/취소 혹은 강제 취소로 인해 주문이 취소된 경우 허브 재고 복원 이벤트 발행
		if (order.getStatus() == OrderStatus.CANCELLED) {
			orderEvents.cancelled(order);
		}
	}

	@Override
	public void handleDeliveryStatusUpdate(DeliveryUpdatePayload payload) {
		String status = payload.deliveryStatus();

		if ("CANCELLED".equals(status) || "FAILED".equals(status)) {
			this.handleDeliveryRollback(payload, false);
		} else if ("COMPLETED".equals(status) || "DELIVERED".equals(status)) {
			this.handleDeliveryCompletion(payload);
		} else {
			log.warn("[미처리 배송 상태] orderId: {}, status: {}", payload.orderId(), status);
			throw new IllegalArgumentException("지원하지 않는 배송 상태입니다: " + status);
		}
	}
}
