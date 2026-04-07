package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

public record HubData(
		UUID hubId,
		String name,
		String fullAddress
) {
	public String hubName() {
		return name;
	}

	public String getAddress() {
		return fullAddress;
	}
}
