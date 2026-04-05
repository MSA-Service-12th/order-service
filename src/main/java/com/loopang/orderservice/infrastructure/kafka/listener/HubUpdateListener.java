package com.loopang.orderservice.infrastructure.kafka.listener;

import com.loopang.common.event.OutboxEvent;
import com.loopang.common.messaging.IdempotentConsumer;
import com.loopang.common.util.JsonUtil;
import com.loopang.orderservice.application.service.OrderCommandService;
import com.loopang.orderservice.domain.event.payload.HubUpdatePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

// 허브 -> 주문 방향 이벤트 수신
// 허브 재고 차감 -> 주문 엔티티의 상태를 pending에서 wait_to_approval로 변경
// 허브 재고 차감 실패 -> 주문 엔티티의 상태를 pending에서 cancelled로 변경
@Slf4j
@Component
@RequiredArgsConstructor
public class HubUpdateListener {

	private final OrderCommandService orderCommandService;
	private final JsonUtil jsonUtil;

	@IdempotentConsumer("hub-update-group")
	@KafkaListener(topics = "${topics.hub.stock-updated}", groupId = "hub-group")
	public void onHubUpdate(Message<String> message, Acknowledgment ack) {
		
		String payloadStr = message.getPayload();
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
		
		log.info("Received hub update message: {}, messageId: {}", payloadStr, messageId);

		try {
			// 1. OutboxEvent로 역직렬화
			OutboxEvent outboxEvent = jsonUtil.fromJson(message.getPayload(), OutboxEvent.class);
			
			// 2. OutboxEvent의 payload를 HubUpdatePayload로 변환
			String payloadJson = jsonUtil.toJson(outboxEvent.payload());
			HubUpdatePayload payload = jsonUtil.fromJson(payloadJson, HubUpdatePayload.class);

			// 3. 주문 서비스 로직 호출 (재고 확인 결과 반영)
			orderCommandService.handleInventoryResult(payload);

			// 4. 메시지 처리 성공 확인 (Acknowledge)
			ack.acknowledge();
			
		} catch (Exception e) {
			log.error("Error processing hub update message: {}", messageId, e);
			// 실패 시 별도의 에러 처리 로직(DLT 등)이 필요할 수 있습니다.
			// 여기서는 ack를 호출하지 않아 재시도를 유도하거나 스킵할 수 있습니다.
		}
	}
}
