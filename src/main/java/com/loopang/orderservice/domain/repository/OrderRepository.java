package com.loopang.orderservice.domain.repository;

import com.loopang.orderservice.domain.entity.Order;


public interface OrderRepository {

	Order save(Order order);
}
