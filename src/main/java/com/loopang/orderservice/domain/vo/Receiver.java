package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Receiver {

	@Embedded
	private CompanyInfo companyInfo;

	@Embedded
	private HubInfo hubInfo;

	@Transient
	private Address address;

	@Transient
	private String slackId;

	public static Receiver create(CompanyInfo companyInfo, HubInfo hubInfo, Address address, String slackId) {
		return new Receiver(companyInfo, hubInfo, address, slackId);
	}
}
