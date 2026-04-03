package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.entity.Order;
import com.loopang.orderservice.domain.vo.UserType;

import java.util.UUID;

public interface OrderAccess {

    void validateCreateAccess(UserType userType);

    void validateListSearchAccess(UserType userType);

    //
    void validateReadAccess(UUID userId, UserType userType, UUID correlationId, Order order);

    void validateUpdateDeleteAccess(UUID userId, UserType userType, UUID managedHubId, Order order);
}
