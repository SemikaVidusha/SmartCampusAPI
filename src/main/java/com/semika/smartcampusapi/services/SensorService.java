/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.semika.smartcampusapi.services;

import com.semika.smartcampusapi.exceptions.LinkedResourceNotFoundException;
import com.semika.smartcampusapi.models.Room;
import com.semika.smartcampusapi.models.Sensor;
import java.util.*;

public class SensorService {

    private static Map<String, Sensor> sensors = new HashMap<>();
    private RoomService roomService = new RoomService();

    public Sensor addSensor(Sensor sensor) {

        // Validate room exists
        Room room = roomService.getRoom(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Room with id '" + sensor.getRoomId() + "' does not exist."
            );
        }

        sensors.put(sensor.getId(), sensor);

        // Link sensor to room
        room.getSensorIds().add(sensor.getId());

        return sensor;
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }
    
    public Sensor getSensorById(String id) {
        return sensors.get(id);
    }
}
