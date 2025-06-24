package com.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SurveyManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveyManagementSystemApplication.class, args);
	}

}
