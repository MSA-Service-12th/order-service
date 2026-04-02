package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;

public interface OrderCompanyValidator {

	void validate(Supplier supplier, Receiver receiver);
}
