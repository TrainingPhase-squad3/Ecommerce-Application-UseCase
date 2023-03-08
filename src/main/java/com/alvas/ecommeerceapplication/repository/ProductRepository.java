package com.alvas.ecommeerceapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvas.ecommeerceapplication.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
