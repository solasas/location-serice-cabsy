package com.cabsy.location.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabsy.location.dto.DriverLocationResponse;
import com.cabsy.location.dto.LocationPingRequest;
import com.cabsy.location.service.LocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/ping")
    public ResponseEntity<Void> ping(@Valid @RequestBody LocationPingRequest request) {
        locationService.updateDriverLocation(
                request.driverId(), request.latitude(), request.longitude(), request.available());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{driverId}")
    public DriverLocationResponse getLocation(@PathVariable String driverId) {
        return locationService.getDriverLocation(driverId);
    }
}
