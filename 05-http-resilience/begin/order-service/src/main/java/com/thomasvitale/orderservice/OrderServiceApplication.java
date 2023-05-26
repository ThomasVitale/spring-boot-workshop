package com.thomasvitale.orderservice;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}

@ConfigurationProperties(prefix = "order")
record OrderProperties(URI instrumentService){}

@RestController
@RequestMapping("/orders")
class OrderController {

	private final InstrumentClient instrumentClient;

	OrderController(InstrumentClient instrumentClient) {
		this.instrumentClient = instrumentClient;
	}

	@PostMapping
	Mono<Order> orderBook(@RequestBody OrderRequest orderRequest) {
		return Mono.empty();
	}

}

interface InstrumentClient {}

record Instrument(Long id, String name){}

record Order(Long instrumentId, String title, boolean approved){}

record OrderRequest(Long instrumentId, int quantity){}
