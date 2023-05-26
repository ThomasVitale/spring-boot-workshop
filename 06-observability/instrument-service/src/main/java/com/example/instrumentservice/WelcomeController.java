package com.example.instrumentservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WelcomeController {

  @GetMapping("/")
  String getMessage() {
    return "Welcome to the Instrument Service!";
  }

}
