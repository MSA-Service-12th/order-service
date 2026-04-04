package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.HubData;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.service.dto.ReceiverData;
import com.loopang.orderservice.domain.service.dto.SupplierData;

public interface OrderValidator {

	void validateCompanyAndItem(SupplierData supplierData, ReceiverData receiverData, ItemData itemData);
	void validateItem(ItemData itemData);
	void validateHub(HubData hubData);
}
