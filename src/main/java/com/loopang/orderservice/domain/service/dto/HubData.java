package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

public record HubData(
		UUID hubId,
		String hubName,
		HubAddressData address
) {
	public String getAddress() {
		return address.fullAddress();
	}
}
