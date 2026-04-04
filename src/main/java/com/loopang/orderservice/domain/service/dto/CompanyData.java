package com.loopang.orderservice.domain.service.dto;

import com.loopang.orderservice.domain.vo.CompanyType;

import java.util.UUID;

public record CompanyData(
		UUID id,
		String name,
		CompanyType companyType,
		HubInfoData hub,
		String address
) {
	public UUID getHubId() {
		return hub != null ? hub.hubId() : null;
	}
}
