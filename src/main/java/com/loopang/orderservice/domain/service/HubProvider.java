package com.loopang.orderservice.domain.service;

import com.loopang.orderservice.domain.service.dto.HubData;

import java.util.UUID;

public interface HubProvider {

	// 업체 도메인에 허브주소가 없기 때문에, 허브 도메인에서 허브를 조회해야 함.
	HubData getHub(UUID hubId);
}
