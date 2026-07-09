package com.cabsy.location.dto;

import java.time.Instant;

public record DriverLocationResponse(
        String driverId,
        double latitude,
        double longitude,
        Instant lastUpdated
) {
}
