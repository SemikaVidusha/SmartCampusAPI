/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.semika.smartcampusapi.resources;

import com.semika.smartcampusapi.models.Sensor;
import com.semika.smartcampusapi.services.SensorService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private SensorService sensorService = new SensorService();

    @POST
    public Sensor addSensor(Sensor sensor) {
        return sensorService.addSensor(sensor);
    }

    @GET
    public List<Sensor> getSensors() {
        return sensorService.getAllSensors();
    }
}
