package com.loopang.orderservice.infrastructure.kafka.listener;

import com.loopang.common.event.OutboxEvent;
import com.loopang.common.util.JsonUtil;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;

// 외부 도메인에서 유입되는 이벤트를 처리하기 위한 공통 규격
public interface InboundEventListener {

	// 정상적인 이벤트 수신 및 비즈니스 로직 처리
	void onEvent(Message<String> message, Acknowledgment ack);

	// 최종 실패 시 DLT(Dead Letter Topic) 처리
	void handleDLT(Message<String> message, Acknowledgment ack);

	// 메시지 페이로드(OutboxEvent)에서 실제 도메인 페이로드를 추출하는 공통 메서드
	default <T> T extractPayload(String messagePayload, JsonUtil jsonUtil, Class<T> payloadClass) {
		try {
			OutboxEvent outboxEvent = jsonUtil.fromJson(messagePayload, OutboxEvent.class);
			String payloadJson = jsonUtil.toJson(outboxEvent.payload());
			return jsonUtil.fromJson(payloadJson, payloadClass);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to extract payload: " + e.getMessage(), e);
		}
	}
}
