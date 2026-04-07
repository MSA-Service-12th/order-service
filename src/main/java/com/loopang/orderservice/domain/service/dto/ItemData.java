package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

public record ItemData(
		UUID id,
		String name,
		UUID companyId
) {
	public UUID itemId() {
		return id;
	}

	public String itemName() {
		return name;
	}
}
