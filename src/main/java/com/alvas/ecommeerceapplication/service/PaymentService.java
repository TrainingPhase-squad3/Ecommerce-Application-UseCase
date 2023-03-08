package com.alvas.ecommeerceapplication.service;

import com.alvas.ecommeerceapplication.dto.PaymentDto;
import com.alvas.ecommeerceapplication.response.ApiResponse;

public interface PaymentService {
	ApiResponse payment(long userId, PaymentDto paymentDto);
}
