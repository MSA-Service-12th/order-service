package com.loopang.orderservice.presentation.dto;

import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSummaryResponseDto {

    private UUID orderId;
    private OrderStatus status;
    private Integer quantity;
    private String supplierName;
    private String receiverName;
    private String itemName;
    private LocalDateTime createdAt;

    public static OrderSummaryResponseDto from(OrderSummaryDto summary) {
        return OrderSummaryResponseDto.builder()
                .orderId(summary.getOrderId())
                .status(summary.getStatus())
                .quantity(summary.getQuantity())
                .supplierName(summary.getSupplierName())
                .receiverName(summary.getReceiverName())
                .itemName(summary.getItemName())
                .createdAt(summary.getCreatedAt())
                .build();
    }
}
