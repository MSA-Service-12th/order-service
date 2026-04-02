package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;
import org.springframework.stereotype.Component;

// 공급업체, 수령업체의 업체타입을 검사 -> 업체타입이 맞지 않으면 예외 발생
// 외부 도메인의 개입 없이 주문 도메인 내부에서 수행하는 서비스 로직이므로 infrastructure가 아닌 domain 패키지 내부에 저장
@Component
public class OrderCompanyValidatorImpl implements OrderCompanyValidator {

	@Override
	public void validate(Supplier supplier, Receiver receiver) {
		if (!supplier.isSupplier()) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		if (!receiver.isReceiver()) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
	}
}
