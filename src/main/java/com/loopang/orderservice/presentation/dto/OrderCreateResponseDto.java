package com.loopang.orderservice.presentation.dto;

import com.loopang.orderservice.application.dto.OrderCreateResultDto;
import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreateResponseDto {
    private UUID orderId;
    private UUID supplierId;
    private UUID supplierHubId;
    private UUID receiverId;
    private UUID receiverHubId;
    private UUID itemId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private UUID createdBy;

    public static OrderCreateResponseDto from(OrderCreateResultDto result) {
        return OrderCreateResponseDto.builder()
                .orderId(result.getOrderId())
                .supplierId(result.getSupplierId())
                .supplierHubId(result.getSupplierHubId())
                .receiverId(result.getReceiverId())
                .receiverHubId(result.getReceiverHubId())
                .itemId(result.getItemId())
                .quantity(result.getQuantity())
                .status(result.getStatus())
                .createdAt(result.getCreatedAt())
                .createdBy(result.getCreatedBy())
                .build();
    }
}
