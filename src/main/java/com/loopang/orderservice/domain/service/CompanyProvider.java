package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;

import java.util.UUID;

public interface CompanyProvider {

	// 공급업체, 수령업체의 정보를 조회
	// 공급업체, 수령업체 VO 구성에는 다소 차이가 존재
	Supplier getSupplier(UUID supplierId);
	Receiver getReceiver(UUID receiverId);
}
