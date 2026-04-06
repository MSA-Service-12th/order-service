package com.loopang.orderservice.domain.event.payload;

import java.util.UUID;

// 배송 -> 주문 방향으로 오는 이벤트 데이터
// 배송 생성, 상태 변경, 완료 및 취소 시 수신
public record DeliveryUpdatePayload(
		UUID orderId,
		UUID departureId,   // 출발지ID (공급업체 허브ID)
		UUID destinationId, // 목적지ID (수령업체 허브ID 또는 수령업체ID)
		UUID deliveryId,    // 생성된 배송ID
		String deliveryStatus // 배송상태 (Enum 매핑 전 원본 문자열)
) { }
