package com.javatechie.spring.batch.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JobCompletionNotificationListener implements JobExecutionListener
{
	@Override
	public void afterJob(JobExecution jobExecution)
	{
		if(jobExecution.getStatus() == BatchStatus.COMPLETED)
		{
			log.info("!!! JOB FINISHED! Time to verify the results");
		}
	}
}
