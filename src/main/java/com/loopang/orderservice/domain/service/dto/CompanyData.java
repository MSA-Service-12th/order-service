package com.loopang.orderservice.domain.service.dto;

import com.loopang.orderservice.domain.vo.CompanyType;

import java.util.UUID;

public record CompanyData(
		UUID id,
		String name,
		CompanyType type,
		UUID hubId,
		String hubName,
		String fullAddress
) {
	public UUID getHubId() {
		return hubId();
	}
}
