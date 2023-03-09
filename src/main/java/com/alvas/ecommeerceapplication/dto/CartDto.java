package com.alvas.ecommeerceapplication.dto;

import java.util.List;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
	@Valid
	List<CartProductDto> productDtos;
}

