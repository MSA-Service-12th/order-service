package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.CompanyData;
import com.loopang.orderservice.domain.service.dto.HubData;
import com.loopang.orderservice.domain.service.dto.ItemData;

public interface OrderValidator {

	void validateCompanyAndItem(CompanyData supplierData, CompanyData receiverData, ItemData itemData);
	void validateCompany(CompanyData companyData);
	void validateItem(ItemData itemData);
	void validateHub(HubData hubData);
}
