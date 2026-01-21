// src/main/java/com/framework/api/TestResource.java

package com.framework.api;

import com.framework.data.entity.PlayerEntity;
import com.framework.service.TestService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/test") // Base path for this resource
public class TestResource {

    @Inject
    TestService testService; // Inject the service we created earlier

    /**
     * Endpoint to test database connectivity and JPA saving/loading.
     * Accessible at: GET http://localhost:8080/test/db-check
     */
    @GET
    @Path("/db-check")
    @Produces(MediaType.APPLICATION_JSON) // Tells Quarkus to return JSON
    public Response dbCheck() {
        try {
            // 1. Call the service method that interacts with the DB
            PlayerEntity foundPlayer = testService.createAndFindTestPlayer();

            // 2. Build a successful response
            return Response.ok()
                    .entity(foundPlayer) // Quarkus will automatically convert the PlayerEntity to JSON
                    .build();

        } catch (Exception e) {
            // 3. Handle any exceptions (like connection failure)
            return Response.serverError()
                    .entity("Database Test FAILED: " + e.getMessage())
                    .build();
        }
    }
}