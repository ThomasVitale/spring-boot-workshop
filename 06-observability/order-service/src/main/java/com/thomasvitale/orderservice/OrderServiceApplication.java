package com.thomasvitale.orderservice;

import java.net.URI;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderServiceApplication {

	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	InstrumentClient instrumentClient(WebClient.Builder builder, OrderProperties orderProperties) {
		WebClient client = builder.baseUrl(orderProperties.instrumentService().toString()).build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(client)).build();
		return factory.createClient(InstrumentClient.class);
	}

}

@ConfigurationProperties(prefix = "order")
record OrderProperties(URI instrumentService){}

@RestController
@RequestMapping("/orders")
class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	private final InstrumentClient instrumentClient;

	OrderController(InstrumentClient instrumentClient) {
		this.instrumentClient = instrumentClient;
	}

	@PostMapping
	Mono<Order> orderInstrument(@RequestBody OrderRequest orderRequest) {
		return instrumentClient.getInstrumentById(orderRequest.instrumentId())
				.doFirst(() -> log.info("Retrieving information for instrument with id: {}", orderRequest.instrumentId()))
				.timeout(Duration.ofSeconds(1), Mono.empty())
				.onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
				.map(instrument -> new Order(instrument.id(), instrument.name(), true))
				.defaultIfEmpty(new Order(orderRequest.instrumentId(), null, false));
	}

}

@HttpExchange("/instruments")
interface InstrumentClient {

	@GetExchange("/{id}")
	Mono<Instrument> getInstrumentById(@PathVariable("id") Long id);

}

record Instrument(Long id, String name){}

record Order(Long instrumentId, String title, boolean approved){}

record OrderRequest(Long instrumentId, int quantity){}
