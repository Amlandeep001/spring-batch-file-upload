package com.javatechie.spring.batch.config;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

//@Slf4j
public class ExceptionSkipPolicy implements SkipPolicy
{
	@Override
	public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException
	{
		return throwable instanceof NumberFormatException;
	}
}
