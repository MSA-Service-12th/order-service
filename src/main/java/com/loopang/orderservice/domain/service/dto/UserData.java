package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

public record UserData(UUID userId, UUID managedHubId, String slackId) {
}
