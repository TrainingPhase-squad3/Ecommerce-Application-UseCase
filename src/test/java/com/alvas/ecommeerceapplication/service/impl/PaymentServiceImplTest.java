package com.alvas.ecommeerceapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alvas.ecommeerceapplication.entity.Cart;
import com.alvas.ecommeerceapplication.entity.CartProduct;
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

@ExtendWith(SpringExtension.class)
public class PaymentServiceImplTest {
	@Mock
	ProductRepository productRepository;

	@Mock
	CartRepository cartRepository;

	@Mock
	WalletRepository walletRepository;

	@Mock
	PaymentRepository paymentRepository;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	PaymentServiceImpl paymentServiceImpl;

	@Test
	void testGetUserPurchasesForMonth_UserIdNotFoundException() {

		Long userId = 1L;
		LocalDate monthDate = LocalDate.now();
		int pageNumber = 0;
		int pageSize = 10;

		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

		Assertions.assertThrows(UserIdNotFoundException.class, () -> {
			paymentServiceImpl.getUserPurchasesForMonth(userId, monthDate, pageNumber, pageSize);
		});
	}

	@Test
	void testGetUserPurchasesForMonth_NoPurchaseHistoryFoundException() {

		Long userId = 1L;
		LocalDate monthDate = LocalDate.of(2023, 3, 1);
		int pageNumber = 0;
		int pageSize = 10;

		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
		Mockito.when(paymentRepository.findByUserUserIdAndPaymentDateBetween(userId, monthDate.withDayOfMonth(1),
				monthDate.withDayOfMonth(monthDate.lengthOfMonth()),
				PageRequest.of(pageNumber, pageSize, Sort.by("paymentDate").descending())))
				.thenReturn(new PageImpl<>(Collections.emptyList()));

		Assertions.assertThrows(NoPurchaseHistoryFoundException.class, () -> {
			paymentServiceImpl.getUserPurchasesForMonth(userId, monthDate, pageNumber, pageSize);
		});
	}

	@Test
	void testGetUserPurchasesForMonth() {
	   
	    Long userId = 1L;
	    LocalDate monthDate = LocalDate.of(2023, 3, 1);
	    int pageNumber = 0;
	    int pageSize = 10;
	    
	    User user = new User();
	    user.setUserId(userId);
	    
	    CartProduct cartProduct = new CartProduct();
	    cartProduct.setProductId(1L);
	    cartProduct.setQuantity(2);
	    
	    Cart cart = new Cart();
	    cart.setTotalPrice(10.0);
	    cart.setCartProducts(Arrays.asList(cartProduct));
	    
	    Payment payment = new Payment();
	    payment.setPaymentDate(LocalDate.now());
	    payment.setCart(cart);
	    payment.setWalletId(1L);
	    payment.setUser(user);
	    
	    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
	    Mockito.when(paymentRepository.findByUserUserIdAndPaymentDateBetween(
	        userId, monthDate.withDayOfMonth(1), monthDate.withDayOfMonth(monthDate.lengthOfMonth()), 
	        PageRequest.of(pageNumber, pageSize, Sort.by("paymentDate").descending())
	    )).thenReturn(new PageImpl<>(Collections.singletonList(payment)));
	    
	  
	    List<PurchaseHistoryResponse> result = paymentServiceImpl.getUserPurchasesForMonth(userId, monthDate, pageNumber, pageSize);
	    

	    Assertions.assertEquals(1, result.size());



	
	
	
	

}}
