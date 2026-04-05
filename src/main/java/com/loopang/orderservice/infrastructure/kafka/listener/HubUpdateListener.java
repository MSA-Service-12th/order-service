package com.loopang.orderservice.infrastructure.kafka.listener;

import com.loopang.common.domain.outbox.Outbox;
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
	@KafkaListener(topics = "${topics.hub.stock-updated}", groupId = "order-group")
	public void onHubUpdate(Message<String> message, Acknowledgment ack) {

		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			// 1. OutboxEvent로 역직렬화
			OutboxEvent outboxEvent = jsonUtil.fromJson(message.getPayload(), OutboxEvent.class);
			// 2. OutboxEvent의 payload를 HubUpdatePayload로 변환
			String payloadJson = jsonUtil.toJson(outboxEvent.payload());
			HubUpdatePayload payload = jsonUtil.fromJson(payloadJson, HubUpdatePayload.class);

			if (payload != null) {
				// 3. 주문 서비스 로직 호출 (재고 확인 결과 반영)
				orderCommandService.handleInventoryResult(payload);
				log.info("허브 재고 차감 완료: {}, messageId: {}", payload.hubId(), messageId);
			}
			// 4. 메시지 처리 성공 확인 (Acknowledge)
			ack.acknowledge();
			
		} catch (Exception e) {
			log.error("허브 재고 부족: {}", messageId, e);

			throw e;
			// 실패 시 별도의 에러 처리 로직(DLT 등)이 필요할 수 있습니다.
		}
	}

	@KafkaListener(topics = "${topics.hub.stock-updated}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		log.error("DLT 수신 - 최종 처리 실패: {}", message.getPayload());
		try {
			OutboxEvent outboxEvent = jsonUtil.fromJson(message.getPayload(), OutboxEvent.class);
			String payloadJson = jsonUtil.toJson(outboxEvent.payload());
			HubUpdatePayload payload = jsonUtil.fromJson(payloadJson, HubUpdatePayload.class);

			if (payload != null) {
				orderCommandService.handleInventoryCheckFailure(payload);
				log.error("주문 수량 확보 최종 실패 처리 완료: orderId={}", payload.orderId());
			}
		} catch (Exception e) {
			log.error("DLT 복구 중 심각한 오류 발생: {}", message.getPayload(), e);
		} finally {
			ack.acknowledge();
		}
	}
	}
