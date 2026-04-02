package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.vo.OrderItemInfo;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;
import org.springframework.stereotype.Component;

@Component
public class OrderValidatorImpl implements OrderValidator {

	@Override
	public void validate(Supplier supplier, Receiver receiver, OrderItemInfo orderItemInfo) {
		if (supplier == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		if (receiver == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
		if (orderItemInfo == null) {
			throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_FOUND);
		}
	}
}
