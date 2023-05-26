# REST Services

## Learning goals

* Defining REST APIs with Spring Web
* Testing applications with integration tests

## Overview

In this excercise, you will see how to implement a REST API in the Instrument Service application
and how to test it.

* Define an `Instrument` record with fields `id` (`Long`) and `name` (`String`).
* Implement GET and POST requests in the `InstrumentController` class (use an in-memory map for data storage, for now).
* Write integration tests to verify the REST API behavior.

## Details

### Data entity

Define the domain entity as a Java record.

```java
record Instrument(Long id, String name) {}
```

### REST API

In Spring, you can design REST APIs either via `@RestController` classes or via `RouterFunctions`. Let's use the first approach. Define an in-memory map to store instruments in `InstrumentController` and implement methods for GET and POST requests for retrieving and creating instruments, respectively.

```java
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
    instruments.put(instrument.id(), instrument)
    return instrument;
  }
  
}
```

The controller makes use of a few annotations:

* `@RestController` is a stereotype annotation marking a class as a Spring component and as a source of handlers for REST endpoints.
* `@RequestMapping` identifies the root path mapping URI for which the class provides handlers ("/instruments").
* `@GetMapping` maps HTTP GET requests onto the specific handler method.
* `@PostMapping` maps HTTP POST requests onto the specific handler method.
* `@ResponseStatus` returns a specific HTTP status if the request is successful.
* `@PathVariable` binds a method parameter to a URI template variable ({id}).
* `@RequestBody` binds a method parameter to the body of a web request.

Finally, run the application and test that it works correctly.

```bash
./gradlew bootRun
```

Add an instrument.

```bash
http :8080/instruments id=1 name="piano"
```

Next, retrieve the full list of instruments.

```bash
http :8080/instruments
```

### Testing

Full integration tests are possible via the `@SpringBootTest` annotation.

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstrumentServiceApplicationTests {

	@Autowired
	WebTestClient webTestClient;

	@Test
	void addBookToCatalog() {
    var instrumentToCreate = new Instrument(1L, "Steinway Piano");

    webTestClient
      .post()
      .uri("/instruments")
      .bodyValue(instrumentToCreate)
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody(Instrument.class).value(actualInstrument -> {
        assertThat(actualInstrument.id()).isEqualTo(instrumentToCreate.id());
        assertThat(actualInstrument.name()).isEqualTo(instrumentToCreate.name());
      });
	}

}
```

When you're done, run the tests as follows:

```bash
./gradlew test --tests InstrumentServiceApplicationTests
```
