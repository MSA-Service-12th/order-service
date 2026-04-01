package com.loopang.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyInfo {

	private UUID companyId;
	private String companyName;

	public static CompanyInfo of(UUID companyId, String companyName) {
		return new CompanyInfo(companyId, companyName);
	}
}
