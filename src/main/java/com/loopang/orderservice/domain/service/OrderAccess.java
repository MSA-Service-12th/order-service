package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.UserType;

import java.util.UUID;

public interface OrderAccess {

    void validateCreateAccess(UserType userType);

    void validateListSearchAccess(UserType userType);

    // 3번째 인자에는 배송ID 또는 담당허브ID 추가
    void validateReadAccess(UUID userId, UserType userType, UUID correlationId, Order order);

    // 3번째 인자에는 배송ID 또는 담당허브ID 추가
    void validateUpdateDeleteAccess(UUID userId, UserType userType, UUID correlationId, Order order);
}
