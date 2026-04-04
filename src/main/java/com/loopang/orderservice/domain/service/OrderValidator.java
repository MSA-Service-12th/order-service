package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.HubData;
import com.loopang.orderservice.domain.service.dto.ItemData;
import com.loopang.orderservice.domain.service.dto.ReceiverData;
import com.loopang.orderservice.domain.service.dto.SupplierData;

public interface OrderValidator {

	void validateOrder(SupplierData supplier, ReceiverData receiver, ItemData itemData,
					   HubData supplierHub, HubData receiverHub);
}
