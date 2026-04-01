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
public class OrderItem {

	private UUID itemId;
	private String itemName;
	private Integer quantity;
	private Integer itemNumber;

	public static OrderItem create(UUID itemId, String itemName, Integer quantity, Integer itemNumber) {
		if (quantity == null || quantity < 1) {
			throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
		}
		return new OrderItem(itemId, itemName, quantity, itemNumber);
	}
}
