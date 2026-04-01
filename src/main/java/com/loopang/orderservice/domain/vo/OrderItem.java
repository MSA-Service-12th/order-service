package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Column;
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

	@Column(name = "item_id", nullable = false)
	private UUID itemId;

	@Column(name = "item_name", length = 255)
	private String itemName;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "item_number", nullable = false)
	private Integer itemNumber;

	public static OrderItem create(UUID itemId, String itemName, Integer quantity, Integer itemNumber) {
		if (quantity == null || quantity < 1) {
			throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
		}
		return new OrderItem(itemId, itemName, quantity, itemNumber);
	}
}
