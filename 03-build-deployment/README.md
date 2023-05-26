# Build and deployment

## Learning goals

* Package and run applications as containers
* Package and run applications as native executables

## Overview

In this excercise, you will see how to package and containerize the Instrument Service application.

* Use the Spring Boot plugin for Gradle to package and run the application as a container image.

* Use the Spring Boot plugin for Gradle to package and run the application as a native executable.

## Details

### Spring Boot as a container image

From a Terminal window, run this command to package the Spring Boot application as a container image.

```bash
./gradlew bootBuildImage
```

Then, run the container on Docker.

```bash
docker run --rm -p 8080:8080 instrument-service
```

Finally, test the application works correctly.

```bash
http :8080/instruments
```

### Spring Boot as a native executable

From a Terminal window, run this command to compile the Spring Boot application as a native executable.

```bash
./gradlew nativeCompile
```

Then, run the application.

```bash
build/native/nativeCompile/03-instrument-service
```

Finally, test the application works correctly.

```bash
http :8080/instruments
```

### Spring Boot on Kubernetes

As a prerequisite, make sure you have [kind](https://kind.sigs.k8s.io) installed.

Next, follow the [quickstart instructions](https://knative.dev/docs/getting-started/quickstart-install) to install a Knative environment on Kubernetes.

At this point, you have a local Kubernetes cluster and you can use the Knative CLI to deploy an application from a container image.

```bash
kn service create band-service --image ghcr.io/thomasvitale/band-service
```

Finally, test the application works correctly.

```bash
http 127.0.0.1.sslip.io
```
