package com.thiago.leiloa_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableSpringDataWebSupport(
	pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
@SpringBootApplication
public class LeiloaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeiloaApiApplication.class, args);
	}

}
