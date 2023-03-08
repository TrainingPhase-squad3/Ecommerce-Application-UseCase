package com.alvas.ecommeerceapplication.response;

import java.time.LocalDate;
import java.util.List;

import com.alvas.ecommeerceapplication.entity.CartProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseHistoryResponse {

	private List<CartProduct> CartProduct;
	private LocalDate paymentDate;
	private Double totalPrice;

}
