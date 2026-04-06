package com.loopang.orderservice.infrastructure.kafka.listener;

import com.loopang.common.messaging.IdempotentConsumer;
import com.loopang.common.util.JsonUtil;
import com.loopang.orderservice.application.service.OrderInboundEventService;
import com.loopang.orderservice.domain.event.payload.DeliveryUpdatePayload;
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
public class DeliveryCreateListener implements InboundEventListener {

	private final OrderInboundEventService orderInboundEventService;
	private final JsonUtil jsonUtil;

	@Override
	@IdempotentConsumer("delivery-create-group")
	@KafkaListener(id = "delivery-create-listener", topics = "${topics.delivery.created}", groupId = "order-group")
	public void onEvent(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			DeliveryUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);
			log.info("[배송 생성 수신] orderId: {}, deliveryId: {}, messageId: {}", payload.orderId(), payload.deliveryId(), messageId);

			orderInboundEventService.handleDeliveryCreation(payload);
			log.info("[배송 생성 처리 완료] orderId: {}, deliveryId: {}, messageId: {}", payload.orderId(), payload.deliveryId(), messageId);

			ack.acknowledge();
		} catch (Exception e) {
			log.error("[배송 생성 처리 실패] (재시도 예정) messageId: {}, error: {}", messageId, e.getMessage());
			throw e;
		}
	}

	@Override
	@KafkaListener(id = "delivery-create-dlt-listener", topics = "${topics.delivery.created}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
		log.error("[DLT 수신] 배송 생성 최종 실패 메시지 도착 - messageId: {}", messageId);

		try {
			DeliveryUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);
			// 생성 DLT이므로 강제 취소 수행
			orderInboundEventService.handleDeliveryRollback(payload, true);
			log.warn("[DLT 처리 성공] 배송 생성 실패로 인한 주문 강제 롤백 및 보상 이벤트 발행 완료 - orderId: {}, messageId: {}", payload.orderId(), messageId);
		} catch (Exception e) {
			log.error("[DLT 복구 치명적 실패] 수동 확인 필요! messageId: {}, error: {}", messageId, e.getMessage(), e);
		} finally {
			ack.acknowledge();
		}
	}
}
