package com.example.instrumentservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstrumentServiceApplicationTests {

	@Autowired
	WebTestClient webTestClient;

	@Test
	void addBookToCatalog() {
			var instrumentToCreate = new Instrument(null, "Steinway Piano");

			webTestClient
							.post()
							.uri("/instruments")
							.bodyValue(instrumentToCreate)
							.exchange()
							.expectStatus().is2xxSuccessful()
							.expectBody(Instrument.class).value(actualInstrument -> {
									assertThat(actualInstrument.id()).isNotNull();
									assertThat(actualInstrument.name()).isEqualTo(instrumentToCreate.name());
							});
	}

}
