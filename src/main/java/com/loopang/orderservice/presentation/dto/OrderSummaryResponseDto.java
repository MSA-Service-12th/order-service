package com.loopang.orderservice.presentation.dto;

import com.loopang.orderservice.application.dto.OrderSummaryDto;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSummaryResponseDto {
    private UUID orderId;
    private UUID supplierId;
    private String supplierName;
    private UUID receiverId;
    private String receiverName;
    private UUID itemId;
    private String itemName;
    private Integer quantity;
    private OrderStatus status;

    public static OrderSummaryResponseDto from(OrderSummaryDto summary) {
        return OrderSummaryResponseDto.builder()
                .orderId(summary.getOrderId())
                .supplierId(summary.getSupplierId())
                .supplierName(summary.getSupplierName())
                .receiverId(summary.getReceiverId())
                .receiverName(summary.getReceiverName())
                .itemId(summary.getItemId())
                .itemName(summary.getItemName())
                .quantity(summary.getQuantity())
                .status(summary.getStatus())
                .build();
    }
}
