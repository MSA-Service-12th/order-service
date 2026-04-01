package com.loopang.orderservice.domain.vo;

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

	@Column(name = "receiver_name", length = 100)
	private String receiverName;

	@Transient
	private String address;

	@AttributeOverrides({
			@AttributeOverride(name = "hubId", column = @Column(name = "receiver_hub_id")),
			@AttributeOverride(name = "hubName", column = @Column(name = "receiver_hub_name"))
	})
	@Embedded
	private HubInfo hubInfo;

	@Transient
	private Contact contact;

	@Builder(access = AccessLevel.PRIVATE)
	private Receiver(UUID receiverId, String receiverName, String address) {
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.address = address;
	}

	public static Receiver of(UUID receiverId, String receiverName, String address) {
		return Receiver.builder()
				.receiverId(receiverId)
				.receiverName(receiverName)
				.address(address)
				.build();
	}

	public void updateHubInfo(HubInfo hubInfo) {
		this.hubInfo = hubInfo;
	}

	public void updateContact(Contact contact) {
		this.contact = contact;
	}
}
