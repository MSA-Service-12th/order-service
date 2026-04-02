package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.vo.OrderItemInfo;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;

public interface OrderValidator {

	void validate(Supplier supplier, Receiver receiver, OrderItemInfo orderItemInfo);
}
