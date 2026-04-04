package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
import com.loopang.orderservice.domain.service.dto.CompanyData;
import com.loopang.orderservice.domain.service.dto.HubData;
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

	@Column(name = "supplier_name", nullable = false, length = 100)
	private String supplierName;

	@Transient
	private CompanyType type;

	@AttributeOverrides({
			@AttributeOverride(name = "hubId", column = @Column(name = "supplier_hub_id", nullable = false)),
			@AttributeOverride(name = "hubName", column = @Column(name = "supplier_hub_name", nullable = false)),
			@AttributeOverride(name = "hubAddress", column = @Column(name = "supplier_hub_address", nullable = false))
	})
	@Embedded
	private HubInfo hubInfo;

	@Builder(access = AccessLevel.PRIVATE)
	private Supplier(UUID supplierId, String supplierName, CompanyType type, HubInfo hubInfo) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.type = type;
		this.hubInfo = hubInfo;
	}

	public static Supplier of(CompanyData companyData, HubData supplierHub) {
		if (companyData.companyType() != CompanyType.SUPPLIER) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		return Supplier.builder()
				.supplierId(companyData.id())
				.supplierName(companyData.name())
				.type(companyData.companyType())
				.hubInfo(HubInfo.of(supplierHub.hubId(), supplierHub.hubName(), supplierHub.getAddress()))
				.build();
	}


	public void updateHubInfo(HubInfo hubInfo) {
		this.hubInfo = hubInfo;
	}

	public UUID getHubId() {
		if (this.hubInfo == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
		return this.hubInfo.getHubId();
	}

	public String getHubName() {
		if (this.hubInfo == null) {
			throw new OrderException(OrderErrorCode.ORDER_HUB_NOT_FOUND);
		}
		return this.hubInfo.getHubName();
	}
}
