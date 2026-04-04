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
				this.status != OrderStatus.WAIT_TO_APPROVAL) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS_TRANSITION); // 상태 변경 불가 예외
		}
		this.orderItem.updateQuantity(quantity);
	}

	public void delete(UUID deletedBy) {
		if (this.status != OrderStatus.PENDING &&
				this.status != OrderStatus.WAIT_TO_APPROVAL) {
			throw new OrderException(OrderErrorCode.ORDER_CANNOT_DELETE);
		}
		if (this.isDeleted()) {
			throw new OrderException(OrderErrorCode.ORDER_ALREADY_DELETED);
		}
		super.delete(deletedBy);
	}

	// 주문 인가 관련

	public boolean isCreatedBy(UUID userId) {
		return this.getCreatedBy() != null && this.getCreatedBy().equals(userId);
	}

	public boolean isManagedByOrInitial(UUID userId, UUID managedHubId) {
		// 1. 직접 담당자로 지정된 경우
		if (this.hubManager != null && Objects.equals(this.hubManager.getHubChargeId(), userId)) {
			return true;
		}
		if (managedHubId == null) {
			return false;
		}

		// 2. 담당자가 아니더라도 해당 주문의 출발 허브 또는 도착 허브의 관리자인 경우
		UUID supplierHubId = Optional.ofNullable(this.supplier)
				.map(Supplier::getHubId)
				.orElse(null);
		UUID receiverHubId = Optional.ofNullable(this.receiver)
				.map(Receiver::getHubId)
				.orElse(null);

		return Objects.equals(supplierHubId, managedHubId) || Objects.equals(receiverHubId, managedHubId);
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
			throw new OrderException(OrderErrorCode.ORDER_ALREADY_DELETED);
		}
		if (!this.status.checkTransition(next)) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_STATUS_TRANSITION);
		}
	}
}
