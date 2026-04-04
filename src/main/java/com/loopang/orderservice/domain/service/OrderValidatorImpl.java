package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.service.dto.CompanyData;
import com.loopang.orderservice.domain.service.dto.HubData;
import org.springframework.stereotype.Component;

import com.loopang.orderservice.domain.service.dto.ItemData;

@Component
public class OrderValidatorImpl implements OrderValidator {

	@Override
	public void validateCompanyAndItem(CompanyData supplierData, CompanyData receiverData, ItemData itemData) {
		if (supplierData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		if (receiverData == null || supplierData.id().equals(receiverData.id())) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_RECEIVER);
		}
		if (itemData == null || !itemData.companyId().equals(supplierData.id())) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_ITEM);
		}
	}

	@Override
	public void validateCompany(CompanyData companyData) {
		if (companyData == null) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
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
