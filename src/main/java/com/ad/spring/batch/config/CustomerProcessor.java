package com.ad.spring.batch.config;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ad.spring.batch.entity.Customer;

@Component
public class CustomerProcessor implements ItemProcessor<Customer, Customer>
{
	@Override
	public Customer process(Customer customer)
	{
		int age = Integer.parseInt(customer.getAge());
		if(age >= 18)
		{
			return customer;
		}
		return null;
	}
}
