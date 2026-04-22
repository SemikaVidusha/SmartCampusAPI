/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.semika.smartcampusapi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.semika.smartcampusapi.resources.DiscoveryResource;
import com.semika.smartcampusapi.resources.RoomResource;
import com.semika.smartcampusapi.resources.SensorResource;
import com.semika.smartcampusapi.exceptions.GlobalExceptionMapper;
import com.semika.smartcampusapi.exceptions.RoomNotEmptyExceptionMapper;
import com.semika.smartcampusapi.exceptions.LinkedResourceNotFoundExceptionMapper;
import com.semika.smartcampusapi.exceptions.SensorUnavailableExceptionMapper;

public class SmartCampusAPI {

    public static final String BASE_URI = "http://localhost:8081/api/v1/";

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
            .register(DiscoveryResource.class)
            .register(RoomResource.class)
            .register(SensorResource.class)
            .register(JacksonFeature.class)
            .register(SensorUnavailableExceptionMapper.class)
            .register(RoomNotEmptyExceptionMapper.class)
            .register(LinkedResourceNotFoundExceptionMapper.class)
            .register(GlobalExceptionMapper.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    public static void main(String[] args) {
        final HttpServer server = startServer();
        System.out.println("Server running at " + BASE_URI);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            server.shutdownNow();
        }
    }
}