package com.loopang.orderservice.domain.vo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supplier {

	@Column(name = "supplier_id", nullable = false)
	private UUID supplierId;

	@Column(name = "supplier_name", length = 100)
	private String supplierName;

	@Column(name = "requirements", nullable = false)
	private String requirements;		// 요청사항

	@AttributeOverrides({
			@AttributeOverride(name = "hubId", column = @Column(name = "supplier_hub_id")),
			@AttributeOverride(name = "hubName", column = @Column(name = "supplier_hub_name"))
	})
	@Embedded
	private HubInfo hubInfo;

	@Builder(access = AccessLevel.PRIVATE)
	private Supplier(UUID supplierId, String supplierName, String requirements) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.requirements = requirements;
	}

	public static Supplier of(UUID supplierId, String supplierName, String requirements) {
		return Supplier.builder()
				.supplierId(supplierId)
				.supplierName(supplierName)
				.requirements(requirements)
				.build();
	}

	public void updateHubInfo(HubInfo hubInfo) {
		this.hubInfo = hubInfo;
	}
}
