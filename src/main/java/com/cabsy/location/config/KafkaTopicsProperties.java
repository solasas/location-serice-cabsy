package com.cabsy.location.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cabsy.kafka.topics")
public record KafkaTopicsProperties(String driverLocationUpdated) {
}
