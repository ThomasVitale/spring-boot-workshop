package com.example.instrumentservice;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/instruments")
public class InstrumentController {
  
  private static final Logger log = LoggerFactory.getLogger(InstrumentController.class);
  private final InstrumentRepository instrumentRepository;

  InstrumentController(InstrumentRepository instrumentRepository) {
    this.instrumentRepository = instrumentRepository;
  }

  @GetMapping
  List<Instrument> getAllInstruments() {
    log.info("Retrieving all instruments");
    return instrumentRepository.findAll();
  }
  
  @GetMapping("{id}")
  Optional<Instrument> getInstrumentById(@PathVariable Long id) {
    log.info("Retrieving instrument with id: {}", id);
    return instrumentRepository.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Instrument addInstrument(@RequestBody @Valid Instrument instrument) {
    log.info("Adding new instrument: {}", instrument.name());
    return instrumentRepository.save(instrument);
  }
  
}
