package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.service.dto.HubData;
import com.loopang.orderservice.domain.service.dto.ReceiverData;
import com.loopang.orderservice.domain.service.dto.SupplierData;
import org.springframework.stereotype.Component;

import com.loopang.orderservice.domain.service.dto.ItemData;

@Component
public class OrderValidatorImpl implements OrderValidator {

	@Override
	public void validateCompanyAndItem(SupplierData supplierData, ReceiverData receiverData, ItemData itemData) {
		if (supplierData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		if (receiverData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
		if (itemData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_ITEM);
		}
		// 상품이 해당 공급 업체의 것인지 확인
		if (!itemData.companyId().equals(supplierData.id())) {
			throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_FOUND); // 혹은 적절한 에러 코드 (상품이 해당 업체 소속이 아님)
		}
	}

	@Override
	public void validateItem(ItemData itemData) {
		if (itemData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_ITEM);
		}
	}

	@Override
	public void validateHub(HubData hubData) {
		// 허브 정보가 존재하는지 확인
		if (hubData == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
	}
}
