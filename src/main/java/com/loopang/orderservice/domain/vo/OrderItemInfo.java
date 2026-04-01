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
public class OrderItemInfo {

	@Column(name = "item_id", nullable = false)
	private UUID itemId;

	@Column(name = "item_name")
	private String itemName;

	public static OrderItemInfo of(UUID itemId, String itemName) {
		return new OrderItemInfo(itemId, itemName);
	}
}
