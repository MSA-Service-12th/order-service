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

    private UUID orderId;
    private LocalDateTime deletedAt;
    private UUID deletedBy;

    public static OrderDeleteCommandDto from(Order order) {
        return OrderDeleteCommandDto.builder()
                .orderId(order.getOrderId())
                .deletedAt(order.getDeletedAt())
                .deletedBy(order.getDeletedBy())
                .build();
    }
}
