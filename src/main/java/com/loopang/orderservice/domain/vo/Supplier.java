package com.loopang.orderservice.domain.vo;

import com.loopang.orderservice.domain.exception.OrderErrorCode;
import com.loopang.orderservice.domain.exception.OrderException;
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
	private Supplier(UUID supplierId, String supplierName, CompanyType type) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.type = type;
	}

	public static Supplier of(UUID supplierId, String supplierName, String companyType) {
		CompanyType type = CompanyType.find(companyType);
		if (type != CompanyType.SUPPLIER) {
			throw new OrderException(OrderErrorCode.ORDER_INVALID_SUPPLIER);
		}
		return Supplier.builder()
				.supplierId(supplierId)
				.supplierName(supplierName)
				.type(CompanyType.find(companyType))
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
