package com.ad.spring.batch.listener;

import org.springframework.batch.core.SkipListener;

import com.ad.spring.batch.entity.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StepSkipListener implements SkipListener<Customer, Number>
{
	@Override // item reader
	public void onSkipInRead(Throwable throwable)
	{
		log.info("A failure on read {} ", throwable.getMessage());
	}

	@Override // item writter
	public void onSkipInWrite(Number item, Throwable throwable)
	{
		log.info("A failure on write {} , {}", throwable.getMessage(), item);
	}

	@SneakyThrows
	@Override // item processor
	public void onSkipInProcess(Customer customer, Throwable throwable)
	{
		log.info("Item {}  was skipped due to the exception  {}", new ObjectMapper().writeValueAsString(customer),
				throwable.getMessage());
	}
}
