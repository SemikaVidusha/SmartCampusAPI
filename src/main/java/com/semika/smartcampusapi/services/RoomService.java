/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.semika.smartcampusapi.services;

import com.semika.smartcampusapi.exceptions.RoomNotEmptyException;
import com.semika.smartcampusapi.models.Room;
import java.util.*;

public class RoomService {

    private static Map<String, Room> rooms = new HashMap<>();

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public Room addRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public void deleteRoom(String id) {
        Room room = rooms.get(id);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(id);
        }

        rooms.remove(id);
    }
}
