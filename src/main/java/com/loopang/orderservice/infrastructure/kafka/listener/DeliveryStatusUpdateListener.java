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
public class DeliveryStatusUpdateListener implements InboundEventListener {

	private final OrderInboundEventService inboundEventService;
	private final JsonUtil jsonUtil;

	@Override
	@IdempotentConsumer("delivery-status-update-group")
	@KafkaListener(id = "delivery-status-update-listener", topics = "${topics.delivery.status-updated}", groupId = "order-group")
	public void onEvent(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			DeliveryUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);
			log.info("[배송 상태 업데이트 수신] orderId: {}, status: {}, messageId: {}", payload.orderId(), payload.deliveryStatus(), messageId);

			inboundEventService.handleDeliveryStatusUpdate(payload);

			ack.acknowledge();
		} catch (Exception e) {
			log.error("[배송 업데이트 처리 실패] (재시도 예정) messageId: {}, error: {}", messageId, e.getMessage());
			throw e;
		}
	}

	@Override
	@KafkaListener(id = "delivery-status-update-dlt-listener", topics = "${topics.delivery.status-updated}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
		log.error("[DLT 수신] 배송 상태 업데이트 최종 실패 메시지 도착 - messageId: {}", messageId);

		try {
			DeliveryUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, DeliveryUpdatePayload.class);
			String status = payload.deliveryStatus();

			if (isCompletionStatus(status)) {
				// [복구] 완료 이벤트인 경우 완료 처리 경로 수행 (이벤트 손실 방지)
				inboundEventService.handleDeliveryCompletion(payload);
				log.warn("[DLT 복구 성공] 배송 완료 상태 강제 반영 완료 - orderId: {}, messageId: {}", payload.orderId(), messageId);
			} else {
				// [롤백] 실패/취소 혹은 알 수 없는 상태인 경우 강제 롤백 수행
				inboundEventService.handleDeliveryRollback(payload, true);
				log.warn("[DLT 처리 성공] 배송 상태 업데이트 실패로 인한 주문 강제 취소 완료 - orderId: {}, messageId: {}", payload.orderId(), messageId);
			}

			// 비즈니스 로직 및 복구 처리가 성공적으로 완료된 경우에만 오프셋 커밋
			ack.acknowledge();
		} catch (Exception e) {
			log.error("[DLT 복구 치명적 실패] 수동 확인 필요! messageId: {}, error: {}", messageId, e.getMessage(), e);
			// 실패 시 ack를 하지 않음으로써 메시지 유실을 방지하고 에러 로그를 통해 가시성 확보
		}
	}

	private boolean isRollbackStatus(String status) {
		return "CANCELLED".equals(status) || "FAILED".equals(status);
	}

	private boolean isCompletionStatus(String status) {
		return "COMPLETED".equals(status) || "DELIVERED".equals(status);
	}
}
