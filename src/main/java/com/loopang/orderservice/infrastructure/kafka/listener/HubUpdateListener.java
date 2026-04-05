package com.loopang.orderservice.infrastructure.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 허브 -> 주문 방향 이벤트 수신
// 허브 재고 차감 -> 주문 엔티티의 상태를 pending에서 wait_to_approval로 변경
// 허브 재고 차감 실패 -> 주문 엔티티의 상태를 pending에서 cancelled로 변경
@Slf4j
@Component
@RequiredArgsConstructor
public class HubUpdateListener {
}
