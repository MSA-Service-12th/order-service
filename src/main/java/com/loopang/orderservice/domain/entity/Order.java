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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "p_order")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseUserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID orderId;

	@Embedded
	private Supplier supplier;	// 공급업체

	@Embedded
	private Receiver receiver;	// 수령업체

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
		if (this.status != OrderStatus.PENDING &&
				this.status != OrderStatus.WAIT_TO_APPROVAL &&
				this.status != OrderStatus.CANCELLED) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS_TRANSITION); // 상태 변경 불가 예외
		}
		this.orderItem.updateQuantity(quantity);
	}

	public void delete(UUID deletedBy) {
		if (this.status != OrderStatus.PENDING &&
				this.status != OrderStatus.WAIT_TO_APPROVAL &&
				this.status != OrderStatus.CANCELLED) {
			throw new OrderException(OrderErrorCode.ORDER_CANNOT_DELETE);
		}
		super.delete(deletedBy);
	}

	public boolean isCreatedBy(UUID userId) {
		return this.getCreatedBy() != null && this.getCreatedBy().equals(userId);
	}

	public boolean isManagedByOrInitial(UUID userId, UUID managedHubId) {
		return Optional.ofNullable(this.hubManager)
				.map(hubManager -> hubManager.getHubChargeId().equals(userId))
				.orElseGet(() -> Objects.equals(this.getSupplier().getHubId(), managedHubId));
	}

	public boolean isAssignedToDelivery(UUID deliveryId) {
		return this.deliveryId != null && this.deliveryId.equals(deliveryId);
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
		if (this.isDeleted()) {
			throw new OrderException(OrderErrorCode.ORDER_CANNOT_DELETE); // 적절한 '삭제된 주문' 관련 에러 코드로 대체 가능
		}
		if (!this.status.checkTransition(next)) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS_TRANSITION);
		}
	}
}
