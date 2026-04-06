package com.loopang.orderservice.infrastructure.kafka.listener;

import com.loopang.common.messaging.IdempotentConsumer;
import com.loopang.common.util.JsonUtil;
import com.loopang.orderservice.application.service.OrderCommandService;
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

	private final OrderCommandService orderCommandService;
	private final JsonUtil jsonUtil;

	@Override
	@IdempotentConsumer("delivery-create-group")
	@KafkaListener(id = "delivery-create-listener", topics = "${topics.delivery.created}", groupId = "order-group")
	public void onEvent(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			DeliveryUpdatePayload payload
					= extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);

			if (payload != null) {
				orderCommandService.handleDeliveryCreation(payload);
				log.info("배송 생성 피드백 반영 완료 - orderId: {}, deliveryId: {}", payload.orderId(), payload.deliveryId());
			}

			ack.acknowledge();
		} catch (Exception e) {
			log.error("배송 생성 메시지 처리 실패 (재시도 예정) - messageId: {}, error: {}", messageId, e.getMessage());
			throw e;
		}
	}

	@Override
	@KafkaListener(topics = "${topics.delivery.created}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		log.error("DLT 수신 - 배송 생성 최종 실패 메시지: {}", message.getPayload());
		try {
			DeliveryUpdatePayload payload
					= extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);

			if (payload != null) {
				orderCommandService.handleDeliveryRollback(payload);
				log.warn("DLT 처리 - 배송 생성 실패로 인한 주문 강제 취소 완료: orderId={}", payload.orderId());
			}
		} catch (Exception e) {
			log.error("DLT 복구 중 오류 발생: {}", e.getMessage(), e);
		} finally {
			ack.acknowledge();
		}
	}
}
