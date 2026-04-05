package com.loopang.orderservice.infrastructure.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 배송 완료 -> 주문 엔티티의 상태를 completed로 변경
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryUpdateListener {
}
