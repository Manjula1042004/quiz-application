// src/main/java/com/quizapp/OnlineQuizAppApplication.java
package com.quizapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class OnlineQuizAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineQuizAppApplication.class, args);
	}
}