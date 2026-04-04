package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.service.DeliveryProvider;
import com.loopang.orderservice.domain.service.dto.CourierData;
import com.loopang.orderservice.domain.vo.OrderStatus;
import com.loopang.orderservice.infrastructure.client.DeliveryFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryProviderImpl implements DeliveryProvider {

	private final DeliveryFeignClient deliveryFeignClient;

	// 조회한 주문 엔티티의 deliveryId를 토대로 현재 조회하려는 주문을 담당한 배송관리자를 확인
	@Override
	public CourierData getCourierId(Order order) {
		if (order.getDeliveryId() == null || order.getStatus() != OrderStatus.ON_DELIVERY) {
			return null;
		}
		return deliveryFeignClient.getCourierByOrder(order.getDeliveryId());
	}
}
