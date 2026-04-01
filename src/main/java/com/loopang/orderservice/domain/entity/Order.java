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
	@AttributeOverrides({
		@AttributeOverride(name = "companyInfo.companyId", column = @Column(name = "supplier_company_id")),
		@AttributeOverride(name = "companyInfo.companyName", column = @Column(name = "supplier_company_name")),
		@AttributeOverride(name = "hubInfo.hubId", column = @Column(name = "supplier_hub_id")),
		@AttributeOverride(name = "hubInfo.hubName", column = @Column(name = "supplier_hub_name")),
		@AttributeOverride(name = "hubInfo.hubAddress", column = @Column(name = "supplier_hub_address"))
	})
	private Supplier supplier;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "companyInfo.companyId", column = @Column(name = "receiver_company_id")),
		@AttributeOverride(name = "companyInfo.companyName", column = @Column(name = "receiver_company_name")),
		@AttributeOverride(name = "hubInfo.hubId", column = @Column(name = "receiver_hub_id")),
		@AttributeOverride(name = "hubInfo.hubName", column = @Column(name = "receiver_hub_name")),
		@AttributeOverride(name = "hubInfo.hubAddress", column = @Column(name = "receiver_hub_address"))
	})
	private Receiver receiver;

	@Embedded
	private OrderItem orderItem;

	@Embedded
	private OrderDetail orderDetail;

	@Embedded
	private OrderManager orderManager;

	@Version
	private Integer version;

	@Builder(access = AccessLevel.PRIVATE)
	private Order(Supplier supplier, Receiver receiver, OrderItem orderItem, OrderDetail orderDetail) {
		this.supplier = supplier;
		this.receiver = receiver;
		this.orderItem = orderItem;
		this.orderDetail = orderDetail;
	}

	public static Order create(Supplier supplier, Receiver receiver, OrderItem orderItem, String description) {
		return Order.builder()
			.supplier(supplier)
			.receiver(receiver)
			.orderItem(orderItem)
			.orderDetail(OrderDetail.create(OrderStatus.PENDING, description))
			.build();
	}

	public void toWaitToApproval() {
		if (this.orderDetail.getStatus() != OrderStatus.PENDING) {
			return;
		}
		this.orderDetail.changeStatus(OrderStatus.WAIT_TO_APPROVAL);
	}

	public void accept(OrderManager manager) {
		if (this.orderDetail.getStatus() != OrderStatus.WAIT_TO_APPROVAL) {
			return;
		}
		this.orderManager = manager;
		this.orderDetail.changeStatus(OrderStatus.ACCEPTED);
	}

	public void startDelivery(UUID deliveryId) {
		if (this.orderManager == null
				|| this.orderDetail.getStatus() != OrderStatus.ACCEPTED) {
			return;
		}
		this.orderManager  = this.orderManager.assignDeliveryId(deliveryId);
		this.orderDetail.changeStatus(OrderStatus.ON_DELIVERY);
	}

	public void complete() {
		if (this.orderDetail.getStatus() != OrderStatus.ON_DELIVERY) {
			return;
		}
		this.orderDetail.changeStatus(OrderStatus.COMPLETED);
	}

	public boolean checkAuthority(UUID currentUserId, String userRole, UUID currentUserHubId) {
		if ("ROLE_MASTER".equals(userRole)) {
			return true;
		}
		if ("ROLE_HUB".equals(userRole)) {
			if (this.orderManager != null && this.orderManager.getHubChargeId() != null) {
				return currentUserId.equals(this.orderManager.getHubChargeId());
			}
			return currentUserHubId != null && this.supplier.checkHubId(currentUserHubId);
		}
		return false;
	}

	public void cancelByManager(UUID userId, String userRole, UUID userHubId) {
		if (!checkAuthority(userId, userRole, userHubId)) {
			throw new IllegalArgumentException("권한이 없습니다.");
		}
		if (this.orderDetail.getStatus() != OrderStatus.PENDING
				&& this.orderDetail.getStatus() != OrderStatus.WAIT_TO_APPROVAL) {
			throw new IllegalStateException("아직 승인되지 않은 주문만 취소 가능합니다.");
		}
		this.orderDetail = OrderDetail.create(OrderStatus.CANCELLED, this.orderDetail.getDescription());
	}

	public void rollback() {
		if (orderDetail.getStatus() != OrderStatus.COMPLETED) {
			orderDetail.changeStatus(OrderStatus.CANCELLED);
		}
	}

	public void assignManager(OrderManager manager) {
		this.orderManager = manager;
	}
}
