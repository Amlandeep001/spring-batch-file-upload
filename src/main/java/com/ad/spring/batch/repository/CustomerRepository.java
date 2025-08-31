package com.ad.spring.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.spring.batch.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>
{
}
