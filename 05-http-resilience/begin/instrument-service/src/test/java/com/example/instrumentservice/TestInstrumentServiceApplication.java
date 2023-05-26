package com.example.instrumentservice;

import org.springframework.boot.SpringApplication;

public class TestInstrumentServiceApplication {
  
  public static void main(String[] args) {
    SpringApplication.from(InstrumentServiceApplication::main)
      .with(TestEnvironmentConfiguration.class)
      .run(args);
  }

}
