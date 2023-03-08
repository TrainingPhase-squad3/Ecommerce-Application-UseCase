package com.alvas.ecommeerceapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvas.ecommeerceapplication.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	

}
