package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

public record UserData(
		UUID hubId,
		String name,
		String slackId
) { }
