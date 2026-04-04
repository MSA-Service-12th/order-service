package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.CompanyData;

import java.util.UUID;

public interface CompanyProvider {

	// 업체 정보를 조회 (공급업체, 수령업체 공통)
	CompanyData getCompany(UUID companyId);
}
