package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.ReceiverData;
import com.loopang.orderservice.domain.service.dto.SupplierData;

import java.util.UUID;

public interface CompanyProvider {

	// 공급업체, 수령업체의 정보를 조회
	// 공급업체, 수령업체 VO 구성에 차이가 있어, 업체 도메인을 조회한다는 점은 동일하지만 별도의 메서드로 정의
	SupplierData getSupplier(UUID supplierId);
	ReceiverData getReceiver(UUID receiverId, String requirements, UserProvider userProvider);
}
