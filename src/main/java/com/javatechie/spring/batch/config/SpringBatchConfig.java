package com.javatechie.spring.batch.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.listener.StepSkipListener;
import com.javatechie.spring.batch.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig
{
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final CustomerRepository customerRepository;
	private final CustomerProcessor customerProcessor;
	private final CustomerItemWriter customerItemWriter;

	@Bean
	@StepScope
	FlatFileItemReader<Customer> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle)
	{
		FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(new File(pathToFIle)));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	private LineMapper<Customer> lineMapper()
	{
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");
		// lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	RepositoryItemWriter<Customer> writer()
	{
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	Step step1(FlatFileItemReader<Customer> itemReader, TaskExecutor taskExecutor)
	{
		return new StepBuilder("slaveStep", jobRepository)
				.<Customer, Customer>chunk(10, platformTransactionManager)
				.reader(itemReader)
				.processor(customerProcessor)
				.writer(customerItemWriter)
				.faultTolerant()
				.listener(skipListener())
				.skipPolicy(skipPolicy())
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	Job runJob(FlatFileItemReader<Customer> itemReader, TaskExecutor taskExecutor)
	{
		return new JobBuilder("importCustomer", jobRepository)
				.flow(step1(itemReader, taskExecutor))
				.end()
				.build();
	}

	@Bean
	SkipPolicy skipPolicy()
	{
		return new ExceptionSkipPolicy();
	}

	@Bean
	SkipListener<?, ?> skipListener()
	{
		return new StepSkipListener();
	}

	@Bean
	TaskExecutor taskExecutor()
	{
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}

}
