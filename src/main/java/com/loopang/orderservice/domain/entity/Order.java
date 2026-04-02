package com.loopang.orderservice.domain.entity;

import com.loopang.common.domain.BaseUserEntity;
import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
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
	@Column(name = "status", nullable = false)
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

	// 주문 생성, 수정, 삭제 관련

	public static Order create(Supplier supplier, Receiver receiver, OrderItem orderItem) {
		return Order.builder()
				.supplier(supplier)
				.receiver(receiver)
				.orderItem(orderItem)
				.build();
	}

	public void updateDeliveryId(UUID deliveryId) {
		this.deliveryId = deliveryId;
	}

	public void updateQuantity(Integer quantity) {
		this.orderItem.updateQuantity(quantity);
	}

	public void delete(UUID deletedBy) {
		if (this.status != OrderStatus.PENDING && this.status != OrderStatus.CANCELLED) {
			throw new OrderException(OrderErrorCode.ORDER_CANNOT_DELETE);
		}
		super.delete(deletedBy);
	}

	// 주문 상태 전이 관련: 주문 프로세스를 진행하면서 주문상태 확인 및 변경

	public void waitToApproval() {
		validateTransition(OrderStatus.WAIT_TO_APPROVAL);
		this.status = OrderStatus.WAIT_TO_APPROVAL;
	}

	public void acceptBy(HubManager hubManager) {
		validateTransition(OrderStatus.ACCEPTED);
		this.hubManager = hubManager;
		this.status = OrderStatus.ACCEPTED;
	}

	public void startDelivery() {
		validateTransition(OrderStatus.ON_DELIVERY);
		this.status = OrderStatus.ON_DELIVERY;
	}

	public void complete() {
		validateTransition(OrderStatus.COMPLETED);
		this.status = OrderStatus.COMPLETED;
	}

	public void cancel() {
		validateTransition(OrderStatus.CANCELLED);
		this.status = OrderStatus.CANCELLED;
	}

	private void validateTransition(OrderStatus next) {
		if (!this.status.checkTransition(next)) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS_TRANSITION);
		}
	}
}
