package com.example.instrumentservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instruments")
public class InstrumentController {
  
  private static final Map<Long,Instrument> instruments = new ConcurrentHashMap<>();

  @GetMapping
  List<Instrument> getAllInstruments() {
    return List.copyOf(instruments.values());
  }
  
  @GetMapping("{id}")
  Optional<Instrument> getInstrumentById(@PathVariable Long id) {
    return Optional.ofNullable(instruments.get(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Instrument addInstrument(@RequestBody Instrument instrument) {
    instruments.put(instrument.id(), instrument);
    return instrument;
  }
  
}
