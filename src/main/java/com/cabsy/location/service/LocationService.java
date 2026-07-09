package com.cabsy.location.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.cabsy.location.config.LocationRedisProperties;
import com.cabsy.location.dto.DriverLocationResponse;
import com.cabsy.location.exception.DriverLocationNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private static final Duration LOCATION_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, String> redisTemplate;
    private final LocationRedisProperties redisProperties;

    public void updateDriverLocation(String driverId, double latitude, double longitude, boolean available) {
        if (available) {
            redisTemplate.opsForGeo().add(
                    redisProperties.driverLocationsKey(), new Point(longitude, latitude), driverId);
        } else {
            redisTemplate.opsForGeo().remove(redisProperties.driverLocationsKey(), driverId);
        }

        String key = locationKey(driverId);
        Map<String, String> fields = Map.of(
                "latitude", String.valueOf(latitude),
                "longitude", String.valueOf(longitude),
                "lastUpdated", Instant.now().toString());
        redisTemplate.opsForHash().putAll(key, fields);
        redisTemplate.expire(key, LOCATION_TTL);
    }

    public DriverLocationResponse getDriverLocation(String driverId) {
        Map<Object, Object> fields = redisTemplate.opsForHash().entries(locationKey(driverId));
        if (fields.isEmpty()) {
            throw new DriverLocationNotFoundException("No location found for driver " + driverId);
        }
        return new DriverLocationResponse(
                driverId,
                Double.parseDouble((String) fields.get("latitude")),
                Double.parseDouble((String) fields.get("longitude")),
                Instant.parse((String) fields.get("lastUpdated")));
    }

    private String locationKey(String driverId) {
        return "driver:location:%s".formatted(driverId);
    }
}
