package com.loopang.orderservice.infrastructure.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topics.order")
public record OrderTopicProperties(
		String pending,
		String accepted
) { }
