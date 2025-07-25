package com.json;

import io.swagger.jaxrs.config.BeanConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class MyApplication extends Application {

    public MyApplication() {
        // Configure Swagger
        BeanConfig config = new BeanConfig();
        config.setTitle("My JAX-RS API");
        config.setVersion("1.0.0");
        config.setBasePath("/api");
        config.setResourcePackage("com.json");  // Package where resources are
        config.setScan(true);  // Enable scanning of JAX-RS resources
    }
}
