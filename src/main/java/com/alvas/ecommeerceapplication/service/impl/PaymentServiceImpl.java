package com.alvas.ecommeerceapplication.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alvas.ecommeerceapplication.dto.PaymentDto;
import com.alvas.ecommeerceapplication.entity.Cart;
import com.alvas.ecommeerceapplication.entity.CartProduct;
import com.alvas.ecommeerceapplication.entity.Payment;
import com.alvas.ecommeerceapplication.entity.Product;
import com.alvas.ecommeerceapplication.entity.User;
import com.alvas.ecommeerceapplication.entity.Wallet;
import com.alvas.ecommeerceapplication.exception.CartNotFoundException;
import com.alvas.ecommeerceapplication.exception.InsufficientFundsException;
import com.alvas.ecommeerceapplication.exception.RequestedQuantityNotAvailableException;
import com.alvas.ecommeerceapplication.exception.UserNotFoundException;
import com.alvas.ecommeerceapplication.exception.WalletExpiredException;
import com.alvas.ecommeerceapplication.exception.WalletNotFoundException;
import com.alvas.ecommeerceapplication.repository.CartRepository;
import com.alvas.ecommeerceapplication.repository.PaymentRepository;
import com.alvas.ecommeerceapplication.repository.ProductRepository;
import com.alvas.ecommeerceapplication.repository.UserRepository;
import com.alvas.ecommeerceapplication.repository.WalletRepository;
import com.alvas.ecommeerceapplication.response.ApiResponse;
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

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	@Override
	@Transactional
	public ApiResponse payment(long userId, PaymentDto paymentDto) {
		logger.warn("if user not registered throws userNotFoundException");
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User with user id:" + userId + " not found"));
		logger.warn("throws CartNotFoundException if cart not found");
		Cart cart = cartRepository.findById(paymentDto.getCartId()).orElseThrow(
				() -> new CartNotFoundException("Cart with cartId:" + paymentDto.getCartId() + " not found"));
		logger.warn("throws exception when wallet for a user not found");
		Wallet wallet = walletRepository.findById(paymentDto.getWalletId()).orElseThrow(
				() -> new WalletNotFoundException("wallet with walletId:" + paymentDto.getWalletId() + " not found"));

		List<Long> productIds = cart.getCartProducts().stream().map(CartProduct::getProductId)
				.collect(Collectors.toList());
		Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
				.collect(Collectors.toMap(Product::getProductId, Function.identity()));

		cart.getCartProducts().forEach(cartProduct -> {
			Product product = productMap.get(cartProduct.getProductId());
			if (cartProduct.getQuantity() > product.getAvailableQuantity()) {
				logger.warn(
						"if the user added the products quantity to cart which are not present in actual quantity of product throws exception");
				throw new RequestedQuantityNotAvailableException();
			}
			product.setAvailableQuantity(product.getAvailableQuantity() - cartProduct.getQuantity());
		});

		productRepository.saveAll(productMap.values());

		double totalBalance = wallet.getBalance();

		if (wallet.getExpiryDate().isAfter(LocalDate.now())) {
			logger.info("wallet is valid");

			if (totalBalance >= cart.getTotalPrice()) {
				logger.info("wallet contains enough amount to purchase");
				wallet.setBalance(totalBalance - cart.getTotalPrice());
				walletRepository.save(wallet);
				Payment payment = new Payment();
				payment.setUser(user);
				payment.setCart(cart);
				payment.setWalletId(wallet.getWalletId());

				paymentRepository.save(payment);
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setMessage("purchase successfull");
				apiResponse.setStatus(HttpStatus.OK);
				logger.info("cart purchased successfully");
				return apiResponse;

			} else {
				logger.warn("exception due to insufficient funds in wallet");
				throw new InsufficientFundsException();
			}

		}
		logger.warn("exception due to usage of expired wallet");
		throw new WalletExpiredException();

	}

}
