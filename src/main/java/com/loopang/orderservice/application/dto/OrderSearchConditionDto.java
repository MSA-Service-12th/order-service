package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.*;

public record OrderSearchConditionDto(
        String keyword,
        String supplierName,
        String receiverName,
        String itemName,
        OrderStatus status
) {}