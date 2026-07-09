package com.cabsy.location.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cabsy.redis")
public record LocationRedisProperties(String driverLocationsKey) {
}
