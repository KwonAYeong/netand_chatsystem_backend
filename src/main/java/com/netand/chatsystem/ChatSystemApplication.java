package com.netand.chatsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZonedDateTime;
import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class ChatSystemApplication {

	public static void main(String[] args) { SpringApplication.run(ChatSystemApplication.class, args);
	}

}
