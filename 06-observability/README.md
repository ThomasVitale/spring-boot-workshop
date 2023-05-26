# Observability

## Learning goals

* Exploring the Spring Boot Actuator endpoints
* Working with Micrometer for metrics and traces
* Experimenting with observability capabilities in Grafana

## Overview

In this excercise, you will get familiar with the observability features in Spring Boot.

* Use Spring Boot Actuator to expose monitoring and management endpoints.
* Expose Prometheus metrics with Micrometer.
* Enable distributed tracing with OpenTelemetry and Micrometer.
* Use the Grafana observability stack.

## Details

### Run the observability stack

Run this command to start up all the services for the Grafana observability stack:

```bash
docker-compose up -d
```

### Run the applications

Run Instrument Service (`./gradlew bootTestRun`) and create some instruments (at least three). Then, run Order Service (`./gradlew bootRun`) and verify that it works correctly.

```bash
http :8181/orders instrumentId=1 quantity=7
```

### Explore

Next, feel free to explore the several endpoints exposed by Spring Boot Actuator. For example, for Instrument Service, you can get the full list of endpoints as follows.

```bash
http :8080/actuator
```

For example, check the endpoints for `health`, `flyway`, `configprops`, `threaddump`, `heapdump`. 

From Grafana (`http://localhost:3000`), explore the following functionality:

* querying logs from Loki;
* accessing traces from Tempo;
* inspecting metrics from Prometheus;
* visualizing metrics from Grafana.
