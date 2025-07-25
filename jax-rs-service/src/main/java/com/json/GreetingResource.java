package com.json;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "Greeting API", tags = {"Greeting"})
@Path("/greet")
public class GreetingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get a greeting message", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved greeting message"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public String getGreeting() {
        return "{\"message\": \"Hello, world!\"}";
    }
}
