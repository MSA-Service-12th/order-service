package com.loopang.orderservice.domain.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record UserData(
		@JsonProperty("hubId") UUID hubId,
		@JsonProperty("name") String name,
		@JsonProperty("slackId") String slackId
) { }
