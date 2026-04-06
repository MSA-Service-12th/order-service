package com.loopang.orderservice.infrastructure.kafka.listener;

import com.loopang.common.messaging.IdempotentConsumer;
import com.loopang.common.util.JsonUtil;
import com.loopang.orderservice.application.service.OrderInboundEventService;
import com.loopang.orderservice.domain.event.payload.HubUpdatePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubUpdateListener implements InboundEventListener {

	private final OrderInboundEventService orderInboundEventService;
	private final JsonUtil jsonUtil;

	@Override
	@IdempotentConsumer("hub-update-group")
	@KafkaListener(id = "hub-update-listener", topics = "${topics.hub.stock-updated}", groupId = "order-group")
	public void onEvent(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			HubUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, HubUpdatePayload.class);
			log.info("[허브 재고 결과 수신] orderId: {}, balance: {}, messageId: {}", payload.orderId(), payload.balance(), messageId);

			orderInboundEventService.handleInventoryResult(payload);
			log.info("[허브 재고 결과 처리 완료] orderId: {}, messageId: {}", payload.orderId(), messageId);

			ack.acknowledge();
		} catch (Exception e) {
			log.error("[허브 업데이트 처리 실패] (재시도 예정) messageId: {}, error: {}", messageId, e.getMessage());
			throw e;
		}
	}

	@Override
	@KafkaListener(id = "hub-update-dlt-listener", topics = "${topics.hub.stock-updated}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
		log.error("[DLT 수신] 허브 재고 업데이트 최종 실패 메시지 도착 - messageId: {}", messageId);

		try {
			HubUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, HubUpdatePayload.class);
			orderInboundEventService.handleInventoryCheckFailure(payload);
			log.warn("[DLT 처리 성공] 주문 강제 취소 및 보상 이벤트 발행 완료 - orderId: {}, messageId: {}", payload.orderId(), messageId);
		} catch (Exception e) {
			log.error("[DLT 복구 치명적 실패] 수동 확인 필요! messageId: {}, error: {}", messageId, e.getMessage(), e);
		} finally {
			ack.acknowledge();
		}
	}
}
