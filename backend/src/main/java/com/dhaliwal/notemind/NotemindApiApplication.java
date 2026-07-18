package com.dhaliwal.notemind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NotemindApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotemindApiApplication.class, args);
	}

}
