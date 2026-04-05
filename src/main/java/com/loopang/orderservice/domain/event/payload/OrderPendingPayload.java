package com.loopang.orderservice.domain.event.payload;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.OrderItem;
import com.loopang.orderservice.domain.vo.Supplier;

import java.util.UUID;

// 주문 생성 후 주문 -> 허브 방향으로 보낼 이벤트
// 이 이벤트 메시지의 데이터를 활용하여 재고 확인 및 차감을 수행
public record OrderPendingPayload(
		UUID orderId,
		UUID supplierId,
		UUID supplierHubId,
		UUID itemId,
		Integer quantity,
		String orderStatus
) {
	public static OrderPendingPayload from(Order order) {
		Supplier supplier = order.getSupplier();
		OrderItem item = order.getOrderItem();

		return new OrderPendingPayload(
				order.getOrderId(),
				supplier.getSupplierId(),
				supplier.getHubId(),
				item.getItemId(),
				item.getQuantity(),
				order.getStatus().name()
		);
	}
}
