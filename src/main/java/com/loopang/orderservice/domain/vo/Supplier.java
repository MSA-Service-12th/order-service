package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Supplier {

	@Embedded
	private CompanyInfo companyInfo;

	@Embedded
	private HubInfo hubInfo;

	public static Supplier create(CompanyInfo companyInfo, HubInfo hubInfo) {
		return new Supplier(companyInfo, hubInfo);
	}

	public boolean checkHubId(UUID currentHubId) {
		return this.hubInfo.getHubId().equals(currentHubId);
	}
}
