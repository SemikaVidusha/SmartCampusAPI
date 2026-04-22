/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.semika.smartcampusapi.services;

import com.semika.smartcampusapi.exceptions.SensorUnavailableException;
import com.semika.smartcampusapi.models.Sensor;
import com.semika.smartcampusapi.models.SensorReading;
import java.util.*;

public class SensorReadingService {

    private static Map<String, List<SensorReading>> readings = new HashMap<>();
    private SensorService sensorService = new SensorService();

    public List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }

    public SensorReading addReading(String sensorId, SensorReading reading) {

        Sensor sensor = sensorService.getSensorById(sensorId);

        if (sensor == null) {
            throw new RuntimeException("Sensor not found: " + sensorId);
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId);
        }

        // FIX 1: Auto-assign UUID if id is missing
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // FIX 2: Auto-assign current timestamp if missing
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        readings.putIfAbsent(sensorId, new ArrayList<>());
        readings.get(sensorId).add(reading);

        // Update parent sensor's current value
        sensor.setCurrentValue(reading.getValue());

        return reading;
    }
}