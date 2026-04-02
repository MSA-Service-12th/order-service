package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubInfo {

	@Column(nullable = false)
	private UUID hubId;

	@Column(nullable = false)
	private String hubName;

	@Column(nullable = false)
	private String hubAddress;

	public static HubInfo of(UUID hubId, String hubName, String hubAddress) {
		return new HubInfo(hubId, hubName, hubAddress);
	}
}
