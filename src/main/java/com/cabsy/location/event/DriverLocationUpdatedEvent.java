package com.cabsy.location.event;

import java.time.Instant;

public record DriverLocationUpdatedEvent(
        String driverId,
        double latitude,
        double longitude,
        Instant timestamp,
        boolean available
) {
}
