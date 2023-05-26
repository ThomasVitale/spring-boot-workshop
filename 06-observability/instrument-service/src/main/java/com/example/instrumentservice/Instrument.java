package com.example.instrumentservice;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotEmpty;

public record Instrument(
  @Id
  Long id,
  @NotEmpty
  String name
) {}
