package com.netand.chatsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.ZonedDateTime;
import java.util.TimeZone;

@SpringBootApplication
public class ChatSystemApplication {

	public static void main(String[] args) {
		// 타임존 및 시간 로그 찍기
		System.out.println("서버 현재 시간: " + ZonedDateTime.now());
		System.out.println("서버 타임존: " + TimeZone.getDefault().getID());

		SpringApplication.run(ChatSystemApplication.class, args);
	}

}
