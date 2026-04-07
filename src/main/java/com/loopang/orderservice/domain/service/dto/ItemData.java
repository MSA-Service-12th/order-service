package com.loopang.orderservice.domain.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ItemData(
		@JsonProperty("id") UUID id,
		@JsonProperty("name") String name,
		@JsonProperty("companyId") UUID companyId
) {
	public UUID itemId() {
		return id;
	}

	public String itemName() {
		return name;
	}
}
