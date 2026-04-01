package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;

public interface OrderCompanyValidator {

	default void validate(Supplier supplier, Receiver receiver) {
		if (!supplier.isSupplier()) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		if (!receiver.isReceiver()) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
	}
}
