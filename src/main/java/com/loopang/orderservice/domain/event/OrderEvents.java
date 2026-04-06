package com.loopang.orderservice.domain.event;

import com.loopang.orderservice.domain.entity.Order;

// 주문 -> 허브 방향 이벤트(pending)): 주문한 상품의 재고를 허브에서 확인한다.
// 주문 -> 배송 방향 이벤트(accepted): 생성한 주문 내역을 바탕으로 배송 엔티티를 생성한다.
public interface OrderEvents {

	void pending(Order order);
	void accepted(Order order);
	void cancelled(Order order);
}
