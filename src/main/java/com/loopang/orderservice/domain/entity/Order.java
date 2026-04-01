package com.loopang.orderservice.domain.entity;

import com.loopang.common.domain.BaseUserEntity;
import com.loopang.orderservice.domain.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseUserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID orderId;

	@Embedded
	private Supplier supplier;

	@Embedded
	private Receiver receiver;

	@Embedded
	private OrderItem orderItem;

	@Embedded
	private HubManager hubManager;

	@Column(name = "delivery_id")
	private UUID deliveryId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private OrderStatus status;

	@Version
	private Integer version;

	@Builder(access = AccessLevel.PRIVATE)
	private Order(Supplier supplier, Receiver receiver, OrderItem orderItem) {
		this.supplier = supplier;
		this.receiver = receiver;
		this.orderItem = orderItem;
		this.status = OrderStatus.PENDING;
	}

	public static Order create(Supplier supplier, Receiver receiver, OrderItem orderItem) {
		return Order.builder()
				.supplier(supplier)
				.receiver(receiver)
				.orderItem(orderItem)
				.build();
	}

	public void updateHubManager(HubManager hubManager) {
		this.hubManager = hubManager;
	}

	public void updateDeliveryId(UUID deliveryId) {
		this.deliveryId = deliveryId;
	}

	public void updateQuantity(Integer quantity) {
		this.orderItem.updateQuantity(quantity);
	}
}
