package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.service.dto.ItemData;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {

	@Embedded
	private OrderItemInfo orderItemInfo;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "item_number", nullable = false)
	private Integer itemNumber = 1;


	@Builder(access = AccessLevel.PRIVATE)
	private OrderItem(ItemData itemData, int quantity, int itemNumber) {
		this.orderItemInfo = OrderItemInfo.from(itemData);
		this.quantity = quantity;
		this.itemNumber = itemNumber;
	}

	public static OrderItem of(ItemData itemData, int quantity, int itemNumber) {
		checkQuantity(quantity);
		return OrderItem.builder()
				.itemData(itemData)
				.quantity(quantity)
				.itemNumber(itemNumber)
				.build();
	}

	public void updateQuantity(Integer quantity) {
		checkQuantity(quantity);
		this.quantity = quantity;
	}

	private static void checkQuantity(Integer quantity) {
		if (quantity == null || quantity < 1) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_QUANTITY);
		}
	}

	public UUID getItemId() {
		return this.orderItemInfo.getItemId();
	}

	public String getItemName() {
		return this.orderItemInfo.getItemName();
	}
}
