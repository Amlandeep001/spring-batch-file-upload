package com.javatechie.spring.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javatechie.spring.batch.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>
{
}
