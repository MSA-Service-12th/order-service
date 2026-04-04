package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;
import org.springframework.stereotype.Component;

import com.loopang.orderservice.domain.service.dto.ItemData;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderValidatorImpl implements OrderValidator {

	private final HubProvider hubProvider;

	@Override
	public void validateCreateOrder(Supplier supplier, Receiver receiver, ItemData itemData) {

		if (supplier == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		if (receiver == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
		if (itemData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_ITEM);
		}

		// 상품이 해당 공급 업체의 것인지 확인
		if (!itemData.companyId().equals(supplier.getSupplierId())) {
			throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_FOUND); // 혹은 적절한 에러 코드 (상품이 해당 업체 소속이 아님)
		}

		// 허브 정보가 존재하는지 확인
		if (hubProvider.getHub(supplier.getHubId()) == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
		if (hubProvider.getHub(receiver.getHubId()) == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
	}
}
