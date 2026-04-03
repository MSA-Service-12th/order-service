package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;

public interface OrderValidator {

	void validateCreateOrder(Supplier supplier, Receiver receiver, ItemData itemData);
}
