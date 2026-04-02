package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSearchConditionDto {

    private String keyword;
    private String supplierName;
    private String receiverName;
    private String itemName;
    private OrderStatus status;

    public static OrderSearchConditionDto of(
            String keyword, String supplierName, String receiverName,
            String itemName, OrderStatus status) {

        return OrderSearchConditionDto.builder()
                .keyword(keyword)
                .supplierName(supplierName)
                .receiverName(receiverName)
                .itemName(itemName)
                .status(status)
                .build();
    }
}
