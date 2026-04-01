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

public class SmartCampusAPI {

    public static final String BASE_URI = "http://localhost:8081/api/v1/";

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
                
                .register(DiscoveryResource.class)
                .register(RoomResource.class)
                .register(JacksonFeature.class)
                .register(SensorResource.class)
                
                .packages("com.semika.smartcampusapi");

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
