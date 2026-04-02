package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.presentation.dto.OrderCreateRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreateCommandDto {

    private UUID supplierId;
    private UUID receiverId;
    private UUID itemId;
    private Integer quantity;
    private String requirements;

    public static OrderCreateCommandDto from(OrderCreateRequestDto request) {
        return OrderCreateCommandDto.builder()
                .supplierId(request.getSupplierId())
                .receiverId(request.getReceiverId())
                .itemId(request.getItemId())
                .quantity(request.getQuantity())
                .requirements(request.getRequirements())
                .build();
    }
}
