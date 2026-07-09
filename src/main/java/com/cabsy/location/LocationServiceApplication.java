package com.cabsy.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.cabsy.location.config.KafkaTopicsProperties;
import com.cabsy.location.config.LocationRedisProperties;

@SpringBootApplication
@EnableConfigurationProperties({LocationRedisProperties.class, KafkaTopicsProperties.class})
public class LocationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocationServiceApplication.class, args);
	}

}
