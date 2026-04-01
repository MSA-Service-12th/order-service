package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderManager {

	private UUID hubChargeId;
	private String hubChargeName;
	private UUID deliveryId;

	public static OrderManager create(UUID hubChargeId, String hubChargeName) {
		return new OrderManager(hubChargeId, hubChargeName, null);
	}

	public OrderManager assignDeliveryId(UUID deliveryId) {
		return new OrderManager(this.hubChargeId, this.hubChargeName, deliveryId);
	}
}
