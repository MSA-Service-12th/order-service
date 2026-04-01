package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetail {

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	private String description;

	public static OrderDetail create(OrderStatus status, String description) {
		return new OrderDetail(status, description);
	}

	public void changeStatus(OrderStatus newStatus) {
		this.status = newStatus;
	}
}
