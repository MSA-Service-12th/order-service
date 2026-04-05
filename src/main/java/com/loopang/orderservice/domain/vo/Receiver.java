package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.service.dto.CompanyData;
import com.loopang.orderservice.domain.service.dto.HubData;
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

	@Transient
	private CompanyType type;

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

	@Embedded
	private Contact contact;

	@Builder(access = AccessLevel.PRIVATE)
	private Receiver(UUID receiverId, String receiverName, String address, String requirements, CompanyType type, HubInfo hubInfo, Contact contact) {
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.address = address;
		this.requirements = requirements;
		this.type = type;
		this.hubInfo = hubInfo;
		this.contact = contact;
	}

	public static Receiver of(CompanyData companyData, HubData receiverHub, String requirements, String slackId) {
		if (companyData.companyType() != CompanyType.RECEIVER) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
		return Receiver.builder()
				.receiverId(companyData.id())
				.receiverName(companyData.name())
				.address(companyData.address())
				.requirements(requirements)
				.type(companyData.companyType())
				.hubInfo(HubInfo.of(receiverHub.hubId(), receiverHub.hubName(), receiverHub.getAddress()))
				.contact(Contact.of(slackId)) // 헤더에서 받은 슬랙 ID 저장
				.build();
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
