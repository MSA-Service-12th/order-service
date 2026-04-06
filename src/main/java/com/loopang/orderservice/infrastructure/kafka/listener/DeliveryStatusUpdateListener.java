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

// [흐름 2, 3] 배송 완료 및 배송 실패/취소(롤백) 피드백 처리
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryStatusUpdateListener implements InboundEventListener {

	private final OrderCommandService orderCommandService;
	private final JsonUtil jsonUtil;

	@Override
	@IdempotentConsumer("delivery-status-update-group")
	@KafkaListener(id = "delivery-status-update-listener", topics = "${topics.delivery.status-updated}", groupId = "order-group")
	public void onEvent(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			DeliveryUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);

			if (payload != null) {
				if (isRollbackStatus(payload.deliveryStatus())) {
					orderCommandService.handleDeliveryRollback(payload);
					log.warn("배송 롤백 결과 반영 - orderId: {}, status: {}", payload.orderId(), payload.deliveryStatus());
				} else {
					orderCommandService.handleDeliveryCompletion(payload);
					log.info("배송 완료 결과 반영 - orderId: {}, status: {}", payload.orderId(), payload.deliveryStatus());
				}
			}

			ack.acknowledge();
		} catch (Exception e) {
			log.error("배송 업데이트 메시지 처리 실패 (재시도 예정) - messageId: {}, error: {}", messageId, e.getMessage());
			throw e;
		}
	}

	@Override
	@KafkaListener(topics = "${topics.delivery.status-updated}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		log.error("DLT 수신 - 배송 업데이트 최종 실패 메시지: {}", message.getPayload());
		try {
			DeliveryUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);
			if (payload != null) {
				orderCommandService.handleDeliveryRollback(payload);
				log.warn("DLT 처리 - 배송 업데이트 실패로 인한 주문 강제 취소 완료: orderId={}", payload.orderId());
			}
		} catch (Exception e) {
			log.error("DLT 복구 중 오류 발생: {}", e.getMessage(), e);
		} finally {
			ack.acknowledge();
		}
	}

	private boolean isRollbackStatus(String status) {
		return "CANCELLED".equals(status) || "FAILED".equals(status);
	}
}
