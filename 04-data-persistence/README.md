# Data Persistence

## Learning goals

* Persisting data with Spring Data Relational
* Dev services with Docker Compose or Testcontainers
* Testing data persistence with Testcontainers

## Overview

In this excercise, you will see how to implement data persistence in the Instrument Service application.

* Make the `id` field of the `Instrument` record the primary key for the object in the database.
* Define an `InstrumentRepository` interface exposing CRUD methods.
* Use the repository to handle GET and POST requests in `InstrumentController`.
* Use Docker Compose for handling the PostgreSQL service.
* Use Testcontainers for handling the PostgreSQL service, both for development and testing.

## Details

### Data entities and persistence

Spring Data JDBC and Spring Data R2DBC encourage using immutable objects, unlike Spring Data JPA that requires them to be mutable.
Since we're using Spring Data JDBC, we can define domain entities as Records.

An object can be persisted without the need for further annotations or configurations. We use the `@Id` annotation when we would like the framework to manage the id generation for us.

```java
public record Instrument(@Id Long id, String name) {}
```

### Data repositories

Spring Data is based on the concepts of domain-driven design (DDD). Each data entity is modeled as an aggregate, and repositories are used to interact with the database. You can extend one of the interfaces provided by the framework to define an `InstrumentRepository` interface. At startup time, Spring Data will generate an implementation for it automatically.

```java
public interface InstrumentRepository extends ListCrudRepository<Instrument,Long> {}
```

### Dev services with Docker Compose

The project is already configured to use the Docker Compose support in Spring Boot. Run the application normally and Spring Boot will find the Docker Compose file in the project, run a PostgreSQL container, and configure the application to connect to it.

```shell
./gradlew bootRun
```

### Dev services with Testcontainers

Another option for running PostgreSQL as a container at development time is via Testcontainers. Make sure to comment out the Docker Compose dependency before moving on.

First, add a `TestEnvironmentConfiguration` in the test module of your application to define a PostgreSQL container.

```java
@TestConfiguration(proxyBeanMethods = false)
class TestEnvironmentConfiguration {

    @Bean
    @RestartScope
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
      return new PostgreSQLContainer<>("postgres:15");
    }

}
```

The bean is annotated with a couple of additional annotations:

* `@RestartScope` ensures that when the application live reload feature is enabled, the database is not restarted every time there are some code changes.
* `@ServiceConnection` fetches URL and credentials from the test container and pass them to the application to configure the integration with the external service automatically.

Next, define a test application.

```java
public class TestInstrumentServiceApplication {
  
  public static void main(String[] args) {
    SpringApplication.from(InstrumentServiceApplication::main)
      .with(TestEnvironmentConfiguration.class)
      .run(args);
  }

}
```

Finally, you can run the application as follows.

```shell
./gradlew bootTestRun
```

### Integration testing with Testcontainers

The same Testcontainers configuration can be used both for development and testing. You can now import `TestEnvironmentConfiguration` in your integration tests to have a PostgreSQL container automatically provisioned.

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestEnvironmentConfiguration.class)
class InstrumentServiceApplicationTests {
  ...
}
```

When you're done, run the tests as follows:

```bash
./gradlew test --tests InstrumentServiceApplicationTests
```
