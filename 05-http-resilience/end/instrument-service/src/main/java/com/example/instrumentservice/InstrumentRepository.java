package com.example.instrumentservice;

import org.springframework.data.repository.ListCrudRepository;

public interface InstrumentRepository extends ListCrudRepository<Instrument,Long> {}
