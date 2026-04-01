/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.semika.smartcampusapi.resources;

import com.semika.smartcampusapi.models.Room;
import com.semika.smartcampusapi.services.RoomService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private RoomService roomService = new RoomService();

    @GET
    public String getRooms() {
        return "Rooms endpoint is working";
    }

    @POST
    public Room addRoom(Room room) {
        return roomService.addRoom(room);
    }

    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") String id) {
        return roomService.getRoom(id);
    }

    @DELETE
    @Path("/{id}")
    public String deleteRoom(@PathParam("id") String id) {
        roomService.deleteRoom(id);
        return "{ \"message\": \"Room deleted successfully\" }";
    }
}
