package com.loopang.orderservice.domain.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loopang.orderservice.domain.vo.CompanyType;

import java.util.UUID;

public record CompanyData(
		@JsonProperty("id") UUID id,
		@JsonProperty("name") String name,
		@JsonProperty("type") CompanyType type,
		@JsonProperty("hubId") UUID hubId,
		@JsonProperty("hubName") String hubName,
		@JsonProperty("fullAddress") String fullAddress
) {
	public UUID getHubId() {
		return hubId();
	}
}
