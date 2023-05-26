# HTTP and Resilience

## Learning goals

* Using the Spring WebClient
* Defining HTTP interfaces
* Understanding reactive streams and resilience

## Overview

In this exercise, you will see how to use `WebClient` and HTTP interfaces for service-to-service interactions between Order Service and Book Service.

* Define an `InstrumentClient` interface marked as `@HttpExchange` and a GET method to return instruments by id.
* Configure the implementation of the interface via `HttpServiceProxyFactory` and `WebClient`.
* Use the `InstrumentClient` to handle POST requests to the `/orders` endpoint (in `OrderController`).
* Explore the operators available in the reactive stream to make the interaction more resilient.

## Details

### HTTP Interfaces

Instead of working directly with a `WebClient` object, you can define a dedicated HTTP client as an interface.

```java
@HttpExchange("/instruments")
interface InstrumentClient {

	@GetExchange("/{id}")
	Mono<Instrument> getInstrumentById(@PathVariable("id") Long id);

}
```

Then, you need to tell Spring what underlying HTTP client should be used to automatically provide an implementation of the interface. You can do so in your `OrderServiceApplication` class.

```java
@Bean
InstrumentClient instrumentClient(WebClient.Builder builder, OrderProperties orderProperties) {
  WebClient client = builder.baseUrl(orderProperties.instrumentService().toString()).build();
  HttpServiceProxyFactory factory = HttpServiceProxyFactory
    .builder(WebClientAdapter.forClient(client)).build();
  return factory.createClient(InstrumentClient.class);
}
```

Finally, you can use the `InstrumentClient` to implement the method in `OrderController`.

```java
@PostMapping
Mono<Order> orderInstrument(@RequestBody OrderRequest orderRequest) {
  return instrumentClient.getInstrumentById(orderRequest.instrumentId())
    .map(instrument -> new Order(instrument.id(), instrument.name(), true));
}
```

Run Instrument Service (`./gradlew bootTestRun`) and create some instruments (at least three). Then, run Order Service (`./gradlew bootRun`) and verify that it works correctly.

```bash
http :8181/orders instrumentId=1 quantity=7
```

### Reactive streams and resilience

Feel free to make the HTTP interaction between Order Service and Instrument Service more resilient using timeouts, retries, and fallbacks.

#### Timeouts

```java
@PostMapping
Mono<Order> orderInstrument(@RequestBody OrderRequest orderRequest) {
  return instrumentClient.getInstrumentById(orderRequest.instrumentId())
      .timeout(Duration.ofSeconds(1), Mono.empty())
      .map(instrument -> new Order(instrument.id(), instrument.name(), true));
}
```

#### Retries

```java
@PostMapping
Mono<Order> orderInstrument(@RequestBody OrderRequest orderRequest) {
  return instrumentClient.getInstrumentById(orderRequest.instrumentId())
      .timeout(Duration.ofSeconds(1), Mono.empty())
      .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
      .map(instrument -> new Order(instrument.id(), instrument.name(), true));
}
```

#### Fallback

```java
@PostMapping
Mono<Order> orderInstrument(@RequestBody OrderRequest orderRequest) {
  return instrumentClient.getInstrumentById(orderRequest.instrumentId())
    .timeout(Duration.ofSeconds(1), Mono.empty())
    .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
    .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
    .map(instrument -> new Order(instrument.id(), instrument.name(), true))
    .defaultIfEmpty(new Order(orderRequest.instrumentId(), null, false));
}
```

#### Test

Run Instrument Service (`./gradlew bootTestRun`) and create some instruments (at least three). Then, run Order Service (`./gradlew bootRun`) and verify that it works correctly.

```bash
http :8181/orders instrumentId=1 quantity=7
```

Then, try shutting down Instrument Service and then call Order Service again. Notice how the application will not fail, but it will return the configured fallback result: an empty result.
