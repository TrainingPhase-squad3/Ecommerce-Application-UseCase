package com.alvas.ecommeerceapplication.service;

import java.time.LocalDate;
import java.util.List;

import com.alvas.ecommeerceapplication.response.PurchaseHistoryResponse;

public interface PaymentService {
	List<PurchaseHistoryResponse> getUserPurchasesForMonth(Long userId, LocalDate monthStartDate, int pageNumber,
			int pageSize);

}
