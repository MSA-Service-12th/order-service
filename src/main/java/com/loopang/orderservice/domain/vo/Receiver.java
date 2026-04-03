package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Receiver {

	@Column(name = "receiver_id", nullable = false)
	private UUID receiverId;

	@Column(name = "receiver_name", nullable = false, length = 100)
	private String receiverName;

	@Column(name = "receiver_address", nullable = false)
	private String address;

	@Column(name = "requirements", nullable = false)
	private String requirements;		// 요청사항

	@AttributeOverrides({
			@AttributeOverride(name = "hubId", column = @Column(name = "receiver_hub_id", nullable = false)),
			@AttributeOverride(name = "hubName", column = @Column(name = "receiver_hub_name", nullable = false)),
			@AttributeOverride(name = "hubAddress", column = @Column(name = "receiver_hub_address", nullable = false))
	})
	@Embedded
	private HubInfo hubInfo;

	@Transient
	private CompanyType type;

	@Transient
	private Contact contact;

	@Builder(access = AccessLevel.PRIVATE)
	private Receiver(UUID receiverId, String receiverName, String address, String requirements, CompanyType type) {
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.address = address;
		this.requirements = requirements;
		this.type = type;
	}

	public static Receiver of(UUID receiverId, String receiverName, String address, String requirements, String companyType) {
		CompanyType type = CompanyType.find(companyType);
		if (type != CompanyType.RECEIVER) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
		return Receiver.builder()
				.receiverId(receiverId)
				.receiverName(receiverName)
				.address(address)
				.requirements(requirements)
				.type(CompanyType.find(companyType))
				.build();
	}

	public void updateHubInfo(HubInfo hubInfo) {
		this.hubInfo = hubInfo;
	}

	public void updateContact(Contact contact) {
		this.contact = contact;
	}

	public UUID getHubId() {
		if (this.hubInfo == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
		return this.hubInfo.getHubId();
	}

	public String getHubName() {
		if (this.hubInfo == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
		return this.hubInfo.getHubName();
	}
}
