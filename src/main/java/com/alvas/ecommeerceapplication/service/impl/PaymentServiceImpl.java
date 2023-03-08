package com.alvas.ecommeerceapplication.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alvas.ecommeerceapplication.entity.Payment;
import com.alvas.ecommeerceapplication.entity.User;
import com.alvas.ecommeerceapplication.exception.NoPurchaseHistoryFoundException;
import com.alvas.ecommeerceapplication.exception.UserIdNotFoundException;
import com.alvas.ecommeerceapplication.repository.CartRepository;
import com.alvas.ecommeerceapplication.repository.PaymentRepository;
import com.alvas.ecommeerceapplication.repository.ProductRepository;
import com.alvas.ecommeerceapplication.repository.UserRepository;
import com.alvas.ecommeerceapplication.repository.WalletRepository;
import com.alvas.ecommeerceapplication.response.CartProductResponse;
import com.alvas.ecommeerceapplication.response.PurchaseHistoryResponse;
import com.alvas.ecommeerceapplication.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {
	@Autowired
	ProductRepository productRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	PaymentRepository paymentRepository;

	@Autowired
	UserRepository userRepository;

	public List<PurchaseHistoryResponse> getUserPurchasesForMonth(Long userId, LocalDate monthDate, int pageNumber,
			int pageSize) {

		User users = userRepository.findById(userId)
				.orElseThrow(() -> new UserIdNotFoundException("User not found with id: " + userId));

		PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("paymentDate").descending());

		Page<Payment> payments = paymentRepository.findByUserUserIdAndPaymentDateBetween(userId,
				monthDate.withDayOfMonth(1), monthDate.withDayOfMonth(monthDate.lengthOfMonth()), pageRequest);
		if (payments.isEmpty()) {
			throw new NoPurchaseHistoryFoundException("No purchase history found for user " + userId + " in "
					+ monthDate.getMonth() + " " + monthDate.getYear());
		}

		List<PurchaseHistoryResponse> purchaseHistoryResponses = new ArrayList<>();

		payments.getContent().forEach(payment -> {
			PurchaseHistoryResponse purchaseHistoryResponse = new PurchaseHistoryResponse();
			purchaseHistoryResponse.setPaymentDate(payment.getPaymentDate());
			purchaseHistoryResponse.setTotalPrice(payment.getCart().getTotalPrice());
			purchaseHistoryResponse.setWalletId(payment.getWalletId());

			List<CartProductResponse> cartProducts = new ArrayList<>();
			payment.getCart().getCartProducts().forEach(cartProduct -> {
				CartProductResponse cartProductResponse = new CartProductResponse();
				cartProductResponse.setProductId(cartProduct.getProductId());
				cartProductResponse.setQuantity(cartProduct.getQuantity());
				

				cartProducts.add(cartProductResponse);
			});

			purchaseHistoryResponse.setCartProduct(cartProducts);
			purchaseHistoryResponses.add(purchaseHistoryResponse);

		});

		return purchaseHistoryResponses;
	}

}
