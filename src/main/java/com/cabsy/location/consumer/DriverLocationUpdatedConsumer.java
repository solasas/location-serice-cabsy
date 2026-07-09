package com.cabsy.location.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.cabsy.location.event.DriverLocationUpdatedEvent;
import com.cabsy.location.service.LocationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverLocationUpdatedConsumer {

    private final LocationService locationService;

    @KafkaListener(topics = "${cabsy.kafka.topics.driver-location-updated}", containerFactory = "kafkaListenerContainerFactory")
    public void onDriverLocationUpdated(DriverLocationUpdatedEvent event) {
        try {
            locationService.updateDriverLocation(
                    event.driverId(), event.latitude(), event.longitude(), event.available());
        } catch (Exception ex) {
            log.warn("Failed to process driver-location-updated event for driver {}: {}",
                    event.driverId(), ex.getMessage());
        }
    }
}
