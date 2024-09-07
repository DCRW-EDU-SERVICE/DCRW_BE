package com.example.DCRW;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DcrwApplication {

	public static void main(String[] args) {
		SpringApplication.run(DcrwApplication.class, args);
	}

}
