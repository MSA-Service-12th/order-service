package com.loopang.orderservice.domain.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record HubData(
		@JsonProperty("hubId") UUID hubId,
		@JsonProperty("name") String name,
		@JsonProperty("fullAddress") String fullAddress
) {
	public String hubName() {
		return name;
	}

	public String getAddress() {
		return fullAddress;
	}
}
