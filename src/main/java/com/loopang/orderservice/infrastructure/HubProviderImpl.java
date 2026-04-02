package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.HubProvider;
import com.loopang.orderservice.domain.vo.HubInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubProviderImpl implements HubProvider {

	// TODO: FeignClient 연동 후 HubClient 주입

	@Override
	public HubInfo getHub(UUID hubId) {
		throw new UnsupportedOperationException("HubClient Feign 연동이 필요합니다");
	}
}
