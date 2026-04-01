package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubManager {

	@Column(name = "hub_charge_id")
	private UUID hubChargeId;

	@Column(name = "hub_charge_name")
	private String hubChargeName;

	public static HubManager of(UUID userId, String name) {
		return new HubManager(userId, name);
	}
}
