package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.UserType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDeleteCommandDto {

    // 입력 데이터
    private UUID orderId;
    private UUID userId;
    private UserType userType;

    // 출력 데이터 (결과)
    private LocalDateTime deletedAt;
    private UUID deletedBy;

    public static OrderDeleteCommandDto of(UUID orderId, UUID userId, UserType userType) {
        return OrderDeleteCommandDto.builder()
                .orderId(orderId)
                .userId(userId)
                .userType(userType)
                .build();
    }

    public static OrderDeleteCommandDto from(Order order) {
        return OrderDeleteCommandDto.builder()
                .orderId(order.getOrderId())
                .deletedAt(order.getDeletedAt())
                .deletedBy(order.getDeletedBy())
                .build();
    }
}
