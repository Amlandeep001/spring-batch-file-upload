package com.javatechie.spring.batch.config;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.repository.CustomerRepository;

@Component
public class CustomerItemWriter implements ItemWriter<Customer>
{
	@Autowired
	private CustomerRepository repository;

	@Override
	public void write(Chunk<? extends Customer> chunk) throws Exception
	{
		System.out.println("Writer Thread " + Thread.currentThread().getName());
		repository.saveAll(chunk);
	}

}
