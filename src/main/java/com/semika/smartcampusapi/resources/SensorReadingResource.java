/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.semika.smartcampusapi.resources;

import com.semika.smartcampusapi.models.SensorReading;
import com.semika.smartcampusapi.services.SensorReadingService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private SensorReadingService service = new SensorReadingService();
    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return service.getReadings(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading) {
        // FIX: Return 201 Created instead of implicit 200
        SensorReading created = service.addReading(sensorId, reading);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
