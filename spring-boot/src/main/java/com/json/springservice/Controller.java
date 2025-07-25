package com.json.springservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  @GetMapping("/greet")
  public String getGreeting() {
    return "{\"message\": \"Hello, world!\"}";
  }

}
