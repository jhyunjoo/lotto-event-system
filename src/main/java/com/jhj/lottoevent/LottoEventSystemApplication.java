package com.jhj.lottoevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LottoEventSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LottoEventSystemApplication.class, args);
	}

}
