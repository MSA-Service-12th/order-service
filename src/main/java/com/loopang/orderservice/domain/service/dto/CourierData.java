package com.loopang.orderservice.domain.service.dto;

import java.util.UUID;

// 주문 조회 시 배송담당자가 담당한 주문인지를 확인하기 위한 데이터
public record CourierData(
		UUID orderId,
		UUID hubCourierId,
		UUID companyCourierId
) { }
