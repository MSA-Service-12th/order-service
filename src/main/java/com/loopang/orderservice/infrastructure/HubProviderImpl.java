package com.loopang.orderservice.infrastructure;

import com.loopang.orderservice.domain.service.HubProvider;
import com.loopang.orderservice.domain.service.dto.HubData;
import com.loopang.orderservice.infrastructure.client.HubFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubProviderImpl implements HubProvider {

	private final HubFeignClient hubFeignClient;

	@Override
	public HubData getHub(UUID hubId) {
		return hubFeignClient.getHubData(hubId);
	}
}
