package com.loopang.orderservice.domain.event.payload;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;

import java.util.UUID;

// 주문 승인 완료 시 주문 -> 배송 방향으로 이벤트 발송
public record OrderAcceptedPayload(
		UUID orderId,
		UUID supplierId,
		UUID supplierHubId,
		String supplierHubAddress,
		UUID receiverHubId,
		String receiverHubAddress,
		UUID receiverId,
		String receiverName,
		String receiverAddress,
		String receiverSlackId,
		UUID hubChargeId	// 허브관리자 ID
) {
	public static OrderAcceptedPayload from(Order order) {
		Supplier supplier = order.getSupplier();
		Receiver receiver = order.getReceiver();

		if (order.getHubManager() == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS_TRANSITION);
		}

		return new OrderAcceptedPayload(
				order.getOrderId(),
				supplier.getSupplierId(),
				supplier.getHubId(),
				supplier.getHubInfo().getHubAddress(),

				receiver.getHubId(),
				receiver.getHubInfo().getHubAddress(),

				receiver.getReceiverId(),
				receiver.getReceiverName(),
				receiver.getAddress(),
				receiver.getContact().getSlackId(),

				order.getHubManager().getHubManagerId()
		);
	}
}
