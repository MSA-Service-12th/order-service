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
	@KafkaListener(topics = "${topics.hub.stock-updated}", groupId = "order-group")
	public void onHubUpdate(Message<String> message, Acknowledgment ack) {
		Object messageId = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);

		try {
			HubUpdatePayload payload = extractPayload(message.getPayload());

			if (payload == null) {
				log.error("메시지 페이로드 역직렬화 실패 - messageId: {}, raw: {}", messageId, message.getPayload());
				throw new IllegalArgumentException("HubUpdatePayload is null");
			}

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

	@KafkaListener(topics = "${topics.hub.stock-updated}.DLT", groupId = "order-group")
	public void handleDLT(Message<String> message, Acknowledgment ack) {
		log.error("DLT 수신 - 최종 처리 실패 메시지: {}", message.getPayload());
		try {
			HubUpdatePayload payload = extractPayload(message.getPayload());

			if (payload != null) {
				// DLT 단계이므로 주문 강제 취소 로직 호출 (이벤트 발행 포함)
				orderCommandService.handleInventoryCheckFailure(payload);
				log.warn("DLT 처리 - 주문 강제 취소 및 보상 이벤트 발행 완료: orderId={}", payload.orderId());
			} else {
				log.error("DLT 메시지 복구 불가 - 페이로드가 null입니다. 수동 확인 필요: {}", message.getPayload());
			}
		} catch (Exception e) {
			log.error("DLT 복구 중 치명적 오류 발생: {}", e.getMessage(), e);
		} finally {
			// DLT는 더 이상 재시도하지 않으므로 무조건 Acknowledge
			ack.acknowledge();
		}
	}

	private HubUpdatePayload extractPayload(String messagePayload) {
		try {
			OutboxEvent outboxEvent = jsonUtil.fromJson(messagePayload, OutboxEvent.class);
			String payloadJson = jsonUtil.toJson(outboxEvent.payload());
			return jsonUtil.fromJson(payloadJson, HubUpdatePayload.class);
		} catch (Exception e) {
			log.error("Payload 추출 중 오류 발생: {}", e.getMessage());
			return null;
		}
	}
}
