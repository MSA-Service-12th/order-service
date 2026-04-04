package com.loopang.orderservice.application.dto;

import com.loopang.orderservice.domain.vo.OrderStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchConditionDto {

    private String keyword;
    private String supplierName;
    private String receiverName;
    private String itemName;
    private OrderStatus status;

}
