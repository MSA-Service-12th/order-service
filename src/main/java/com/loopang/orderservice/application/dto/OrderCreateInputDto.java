package com.loopang.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateInputDto {

    private UUID supplierId;
    private UUID receiverId;
    private UUID itemId;
    private Integer quantity;
    private String requirements;
}
