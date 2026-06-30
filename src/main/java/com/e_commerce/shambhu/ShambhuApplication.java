package com.e_commerce.shambhu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ShambhuApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShambhuApplication.class, args);
	}

}
