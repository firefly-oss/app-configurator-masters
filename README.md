# app-configurator-masters

Configuration masters gateway microservice for the Firefly OpenCore Banking Platform. This service acts as a reactive API gateway that proxies configuration initialization requests to the downstream Configuration domain service, providing a unified entry point for master configuration management operations.

## Overview

The `app-configurator-masters` service is a Spring Cloud Gateway application built on Spring WebFlux. Rather than implementing configuration logic directly, it leverages the FireflyFramework `@DomainPassthrough` mechanism to declaratively route incoming API requests to the appropriate backend Configuration domain service. This pattern keeps the gateway layer thin and focused on routing, security filtering, and cross-cutting concerns while the actual business logic resides in dedicated domain services.

### Key Features

- **Reactive API Gateway**: Built on Spring Cloud Gateway and Spring WebFlux for non-blocking, high-throughput request handling
- **Declarative Route Passthrough**: Uses FireflyFramework's `@DomainPassthrough` annotation to route requests to backend domain services without boilerplate code
- **Configuration Initialization Routing**: Proxies configuration initialization endpoints to the Configuration domain service
- **WebFlux Security**: Reactive security filter chain configuration with Spring Security for WebFlux
- **Centralized Configuration**: Integrates with Spring Cloud Config Server for externalized configuration management
- **Observability**: Built-in support for Prometheus metrics via Micrometer and distributed tracing via OpenTelemetry (OTLP exporter)
- **API Documentation**: OpenAPI 3.0 documentation via SpringDoc with WebFlux UI support
- **Health Monitoring**: Spring Boot Actuator endpoints for health checks and operational monitoring

## Architecture

This project follows a modular multi-module Maven architecture with clear separation of concerns.

### Modules

- **app-configurator-masters-core**: Business logic and service layer. Depends on FireflyFramework utilities (`fireflyframework-utils`), Spring WebFlux, MapStruct for object mapping, and Lombok for boilerplate reduction. Includes Reactor Test for reactive testing support.
- **app-configurator-masters-interfaces**: DTOs, API contracts, and interface definitions. Depends on the core module, includes Swagger/OpenAPI annotations, Jackson serialization support (including JSR-310 date/time types), and Spring Boot Validation.
- **app-configurator-masters-web**: REST controllers, gateway configuration, security setup, and the Spring Boot application entry point. Depends on the interfaces module and `fireflyframework-application` for shared application infrastructure. Contains the Spring Cloud Gateway routing configuration and all web-layer concerns.

### Technology Stack

- **Java 25**: Latest Java features including virtual threads
- **Spring Boot**: Microservice framework with reactive support
- **Spring WebFlux**: Reactive web framework for non-blocking I/O
- **Spring Cloud Gateway**: API gateway built on Spring WebFlux for reactive routing and filtering
- **Spring Cloud Config**: Externalized configuration management via a central Config Server
- **Spring Security (WebFlux)**: Reactive security filter chain
- **FireflyFramework**: Parent POM (`fireflyframework-parent`), BOM (`fireflyframework-bom` v26.01.01), application infrastructure (`fireflyframework-application`), and utilities (`fireflyframework-utils`) from the [FireflyFramework](https://github.com/fireflyframework/) ecosystem
- **SpringDoc OpenAPI**: API documentation with Swagger UI for WebFlux
- **Micrometer + Prometheus**: Metrics collection and exposition
- **OpenTelemetry (OTLP)**: Distributed tracing export
- **MapStruct**: Compile-time object mapping
- **Lombok**: Boilerplate code reduction
- **Jackson**: JSON serialization/deserialization with JSR-310 support
- **Reactor Test**: Testing utilities for reactive streams

### Project Structure

```
app-configurator-masters/
├── pom.xml                              # Parent POM (multi-module)
├── app-configurator-masters-core/
│   ├── pom.xml                          # Core module POM
│   └── src/main/java/                   # Business logic (services, domain)
├── app-configurator-masters-interfaces/
│   ├── pom.xml                          # Interfaces module POM
│   └── src/main/java/                   # DTOs, API contracts
└── app-configurator-masters-web/
    ├── pom.xml                          # Web module POM
    └── src/main/
        ├── java/com/firefly/app/configurator/web/
        │   ├── AppConfiguratorMastersApplication.java  # Spring Boot entry point
        │   ├── GatewaySecurityConfig.java              # WebFlux security config
        │   └── controllers/
        │       └── ConfiguratorController.java         # Gateway route definitions
        └── resources/
            └── application.yml                         # Application configuration
```

## Setup and Installation

### Prerequisites

- Java 25 or higher
- Maven 3.8 or higher
- Access to the FireflyFramework Maven repository (for `fireflyframework-parent`, `fireflyframework-bom`, `fireflyframework-application`, and `fireflyframework-utils` artifacts)

### Environment Variables

| Variable            | Description                              | Default                                                  |
|---------------------|------------------------------------------|----------------------------------------------------------|
| `CONFIG_SERVER_URL` | URL of the Spring Cloud Config Server    | `https://app-firefly-config-server.dev.soon.es/`         |

Additional configuration properties are resolved at runtime from the Config Server, including:

- `endpoints.domain.configuration` -- Base URL of the Configuration domain service (used by `@DomainPassthrough` route targets)

### Building the Application

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run -pl app-configurator-masters-web
```

The application will start as a reactive gateway service named `app-configurator-masters` (as defined in `application.yml`).

## Main API Endpoints

The following endpoint is defined as a gateway passthrough to the Configuration domain service:

| Method  | Path                            | Description                                  | Target Service   |
|---------|---------------------------------|----------------------------------------------|------------------|
| `*`     | `/api/v1/configuration/init`    | Configuration initialization operations      | Configuration    |

The route is defined declaratively using the `@DomainPassthrough` annotation in `ConfiguratorController.java`. The actual HTTP methods supported depend on the downstream Configuration service implementation.

### API Documentation

When the application is running, the OpenAPI documentation is available at:

- **Swagger UI**: `http://localhost:{port}/webjars/swagger-ui/index.html`
- **OpenAPI spec**: `http://localhost:{port}/v3/api-docs`

## Security Configuration

The gateway security is configured in `GatewaySecurityConfig.java` using Spring Security for WebFlux:

- **CSRF**: Disabled (typical for stateless API gateways)
- **Authorization**: All exchanges are currently permitted (`permitAll()`) at the gateway level; authentication and authorization are expected to be enforced by upstream or downstream services

## Development Guidelines

### Testing

- **Unit Tests**: Use JUnit 5 with Spring Boot Test starter
- **Reactive Tests**: Use `reactor-test` for testing reactive streams and WebFlux components
- Run all tests: `mvn test`

### Code Style

- Use Lombok annotations to reduce boilerplate (getters, setters, builders, etc.)
- Use MapStruct for type-safe object mapping between DTOs and domain objects
- Follow reactive programming patterns with Project Reactor (`Mono`, `Flux`)

### Branching Strategy

- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: Feature branches

## Monitoring

### Actuator Endpoints

Spring Boot Actuator is included and provides operational endpoints:

- `/actuator/health` -- Application health status
- `/actuator/info` -- Build and application information
- `/actuator/prometheus` -- Prometheus-formatted metrics

### Metrics

Micrometer with Prometheus registry is configured for metrics collection. Metrics are exposed via the `/actuator/prometheus` endpoint for scraping by Prometheus.

### Distributed Tracing

OpenTelemetry with the OTLP exporter is configured for distributed tracing across microservices.

## Related Projects

- [FireflyFramework](https://github.com/fireflyframework/) -- Parent framework providing shared infrastructure, BOM, utilities, and application base
- [app-configurator-masters (this repo)](https://github.com/firefly-oss/app-configurator-masters) -- Source repository

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
