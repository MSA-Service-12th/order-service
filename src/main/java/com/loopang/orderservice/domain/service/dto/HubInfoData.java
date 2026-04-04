package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

public record HubInfoData(
		UUID hubId,
		String hubName
) { }
