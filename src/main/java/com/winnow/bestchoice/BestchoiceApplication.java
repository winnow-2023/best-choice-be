package com.winnow.bestchoice;

import com.winnow.bestchoice.config.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class BestchoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestchoiceApplication.class, args);
	}

}
