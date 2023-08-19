package com.winnow.bestchoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BestchoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestchoiceApplication.class, args);
	}

}
