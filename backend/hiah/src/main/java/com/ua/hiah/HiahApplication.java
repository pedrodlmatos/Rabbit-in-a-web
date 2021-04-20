package com.ua.hiah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class HiahApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiahApplication.class, args);
		//ConfigurableApplicationContext context = new SpringApplicationBuilder(HiahApplication.class).headless(false).run(args);
	}
}