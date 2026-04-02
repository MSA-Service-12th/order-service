package com.loopang.orderservice.presentation.dto;

import com.loopang.orderservice.application.dto.OrderDeleteCommandDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDeleteResponseDto {

    private UUID orderId;
    private LocalDateTime deletedAt;
    private UUID deletedBy;

    public static OrderDeleteResponseDto from(OrderDeleteCommandDto result) {
        return OrderDeleteResponseDto.builder()
                .orderId(result.getOrderId())
                .deletedAt(result.getDeletedAt())
                .deletedBy(result.getDeletedBy())
                .build();
    }
}
