package com.json;

import io.swagger.jaxrs.config.BeanConfig;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class MyApplication extends Application {


  public MyApplication() {
    BeanConfig config = new BeanConfig();
    config.setTitle("My API");
    config.setVersion("1.0.0");
    config.setSchemes(new String[]{"http"});
    config.setBasePath("/api");
    config.setResourcePackage("com.json");
    config.setScan(true);
  }

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> classes = new HashSet<>();

    // Your endpoints
    classes.add(HelloResource.class);

    // Swagger resources
    classes.add(io.swagger.jaxrs.listing.ApiListingResource.class);
    classes.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

    return classes;
  }

}
