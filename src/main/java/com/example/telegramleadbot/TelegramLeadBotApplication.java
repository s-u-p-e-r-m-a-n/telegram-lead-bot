package com.example.telegramleadbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TelegramLeadBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramLeadBotApplication.class, args);
	}

}
