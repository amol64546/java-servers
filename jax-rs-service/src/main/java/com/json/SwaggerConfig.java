package com.json;

import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerConfig {

    public SwaggerConfig() {
        BeanConfig config = new BeanConfig();
        config.setTitle("Greeting API");
        config.setVersion("1.0");
        config.setBasePath("/swagger-ui");
        config.setResourcePackage("com.json");
        config.setScan(true);
    }
}
