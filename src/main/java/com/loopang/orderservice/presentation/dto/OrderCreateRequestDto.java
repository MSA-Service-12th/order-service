package com.loopang.orderservice.presentation.dto;

import com.loopang.orderservice.application.dto.OrderCreateInputDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {

	@NotNull(message = "공급업체 ID는 필수입니다.")
	private UUID supplierId;

	@NotNull(message = "수령업체 ID는 필수입니다.")
	private UUID receiverId;

	@NotNull(message = "상품 ID는 필수입니다.")
	private UUID itemId;

	@NotNull(message = "주문 수량은 필수입니다.")
	@Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다.")
	private Integer quantity;

	@NotBlank(message = "요청사항은 필수입니다.")
	private String requirements;


	public OrderCreateInputDto toDto() {
		return new OrderCreateInputDto(supplierId, receiverId, itemId, quantity, requirements);
	}
}
