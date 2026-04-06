package com.loopang.orderservice.infrastructure.kafka.listener;

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
@Slf4j
@Component
@RequiredArgsConstructor
public class HubUpdateListener implements InboundEventListener {

	private final OrderCommandService orderCommandService;
	private final JsonUtil jsonUtil;

	@Override
	@IdempotentConsumer("hub-update-group")
	@KafkaListener(id = "hub-update-listener", topics = "${topics.hub.stock-updated}", groupId = "order-group")
	public void onEvent(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			// 페이로드 추출 실패 시 예외가 발생하여 catch 블록으로 이동함
			HubUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, HubUpdatePayload.class);

			// 주문 서비스 로직 호출 (재고 확인 결과 반영)
			orderCommandService.handleInventoryResult(payload);
			log.info("허브 재고 결과 반영 완료 - orderId: {}, balance: {}, messageId: {}",
					payload.orderId(), payload.balance(), messageId);

			ack.acknowledge();
		} catch (Exception e) {
			log.error("허브 업데이트 메시지 처리 실패 (재시도 예정) - messageId: {}, error: {}",
					messageId, e.getMessage());
			throw e;
		}
	}

	@Override
	@KafkaListener(topics = "${topics.hub.stock-updated}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		log.error("DLT 수신 - 최종 처리 실패 메시지: {}", message.getPayload());
		try {
			HubUpdatePayload payload = extractPayload(message.getPayload(), jsonUtil, HubUpdatePayload.class);

			// DLT 단계이므로 주문 강제 취소 로직 호출 (이벤트 발행 포함)
			orderCommandService.handleInventoryCheckFailure(payload);
			log.warn("DLT 처리 - 주문 강제 취소 및 보상 이벤트 발행 완료: orderId={}", payload.orderId());
		} catch (Exception e) {
			log.error("DLT 복구 중 치명적 오류 발생: {}", e.getMessage(), e);
		} finally {
			ack.acknowledge();
		}
	}
}
