package com.alvas.ecommeerceapplication.dto;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDto {

	private long productId;
	@Min(value = 1,message = "Quantity must be larger than 1")
	private int quantity;

}

