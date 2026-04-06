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

	@Column(name = "hub_manager_id")
	private UUID hubManagerId;

	@Column(name = "hub_manager_name")
	private String hubManagerName;

	public static HubManager of(UUID userId, String name) {
		return new HubManager(userId, name);
	}
}
