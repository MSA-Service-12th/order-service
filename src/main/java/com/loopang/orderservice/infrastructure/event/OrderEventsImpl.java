package com.loopang.orderservice.infrastructure.event;

import com.loopang.common.event.Events;
import com.loopang.common.event.OutboxEvent;
import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.event.OrderEvents;
import com.loopang.orderservice.domain.event.payload.OrderAcceptedPayload;
import com.loopang.orderservice.domain.event.payload.OrderPendingPayload;
import com.loopang.orderservice.infrastructure.kafka.OrderTopicProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OrderTopicProperties.class)
public class OrderEventsImpl implements OrderEvents {

	private final OrderTopicProperties properties;

	@Override
	public void pending(Order order) {
		OutboxEvent outboxEvent = OutboxEvent.withCorrelation(
				getTraceId(),
				"ORDER",
				order.getOrderId(),
				properties.pending(),
				OrderPendingPayload.from(order)
		);
		Events.trigger(outboxEvent);
	}

	@Override
	public void accepted(Order order) {
		OutboxEvent outboxEvent = OutboxEvent.withCorrelation(
				getTraceId(),
				"ORDER",
				order.getOrderId(),
				properties.accepted(),
				OrderAcceptedPayload.from(order)
		);
		Events.trigger(outboxEvent);
	}

	private String getTraceId() {
		String traceId = MDC.get("traceId");
		return StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();
	}
}
