package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.CompanyProvider;
import com.loopang.orderservice.domain.service.HubProvider;
import com.loopang.orderservice.domain.service.UserProvider;
import com.loopang.orderservice.domain.vo.HubInfo;
import com.loopang.orderservice.domain.vo.Receiver;
import com.loopang.orderservice.domain.vo.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyProviderImpl implements CompanyProvider {

	// TODO: FeignClient 연동 후 CompanyClient 주입

	@Override
	public Supplier getSupplier(UUID supplierId, String requirements) {
		throw new UnsupportedOperationException("ItemClient Feign 연동이 필요합니다");
	}

	// TODO: 수령업체 담당자에 관한 추가정보 조회 시 사용자 도메인 조회
	@Override
	public Receiver getReceiver(UUID receiverId, UserProvider userProvider) {
		throw new UnsupportedOperationException("ItemClient Feign 연동이 필요합니다");
	}
}
