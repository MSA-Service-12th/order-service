package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

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

	public static OrderItem of(OrderItemInfo itemInfo, int quantity, int itemNumber) {
		checkQuantity(quantity);
		return new OrderItem(itemInfo, quantity, Math.max(itemNumber, 1));
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
}
