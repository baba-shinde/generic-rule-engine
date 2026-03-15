# Generic Rule Engine (rule-engine)

A lightweight, extensible generic rule engine built with Spring Boot. This project provides a foundation for defining, executing, and managing business rules that evaluate inputs and produce decisions or actions.


## Project metadata
- GroupId: com.bss.app
- ArtifactId: rule-engine
- Version: 0.0.1-SNAPSHOT
- Description: Generic Rule Engine
- Built with: Spring Boot (parent 2.1.6.RELEASE)
- Java target (pom): 25


## Features
- Modular rule execution architecture ready to accept custom rule definitions
- Spring Boot web runtime for embedding the engine into services
- Test support via JUnit 5 and Spring Boot test starter
- POI dependencies included (for possible Excel-based rule/data import)
- Code coverage instrumentation via JaCoCo


## Prerequisites
- Java JDK 25 (or compatible JVM configured to match the project settings)
- Maven 3.6+ (or recent Maven distribution)
- Git (for cloning and contributing)


## Getting started
1. Clone the repository:

   git clone <repo-url>
   cd generic-rule-engine

2. Build the project:

   mvn clean package

   This will compile the code, run tests, and produce a JAR under `target/`.

3. Run the application (development):

   mvn spring-boot:run

   Or run the packaged jar:

   java -jar target/rule-engine-0.0.1-SNAPSHOT.jar


## Running tests

Run unit and integration tests with Maven:

   mvn test

JaCoCo is configured to produce coverage reports during the `verify` phase.


## Project structure (convention)

- src/main/java - application and engine implementation
- src/main/resources - configuration and resource files
- src/test - unit and integration tests
- pom.xml - build configuration and dependencies


## Rule engine concepts (overview)

This README uses general terms since rule formats and APIs are implemented in code. Typical components to expect or extend:

- Rule: a logical unit that evaluates input data and returns a result (boolean/score/action)
- Rule definition: externalized representation of rules (JSON, YAML, DB, or Excel)
- RuleExecutor: runtime component that loads and executes rules against provided data
- RuleContext: carries the input payload, metadata, and intermediate values across rule evaluation
- Actions / Decisions: outputs produced by rules which can be consumed by callers of the engine


## Adding rules

The engine is designed to be extensible. Recommended approaches:

- Simple Java rules: implement the project's rule interface (search project for Rule, RuleExecutor, or similar) and register as Spring beans.
- Externalized rules: store rule definitions as JSON/YAML/DB rows and implement a loader that produces in-memory rule objects consumed by the executor.
- Excel-based rules: POI dependencies are included—create importers to convert spreadsheet rows into rule definitions.


## Extending and customizing

- Implement custom evaluators or predicates for domain-specific checks.
- Add a new RuleLoader implementation to fetch rules from alternative sources (DB, REST, filesystem).
- Integrate caching or hot-reload for rule sets to allow runtime updates without restart.
- Add metrics/tracing around rule evaluation paths for observability.


## Configuration

Spring Boot properties (application.properties or application.yml) can be used to configure rule storage, timeouts, logging levels, and web endpoints. Look in `src/main/resources` for sample configuration files.


## Example usage (conceptual)

1. POST payload to an engine endpoint (if HTTP controllers are present) — payload includes the data to evaluate.
2. Engine loads the active rule set and creates a RuleContext.
3. RuleExecutor evaluates rules and returns a structured result (decision, matched rules, metadata).

Example curl (replace path if controller differs):

   curl -X POST http://localhost:8080/api/rules/evaluate -H "Content-Type: application/json" -d '{"customerId":123, "amount": 5000}'


## Development notes

- The project uses Spring Boot and standard Maven lifecycle. Keep parent/plug-in versions consistent when upgrading Spring Boot.
- The pom includes test and coverage plugins—use `mvn verify` to produce coverage reports.
- The current pom lists Java version 25; ensure the runtime and CI use a compatible JDK.


## Contributing

- Fork the repository and open a pull request with a clear description of changes.
- Include unit tests for new features or bug fixes.
- Follow existing code style and package organization.


## License

This repository includes a LICENSE file at the project root. Review it for license terms.


## Where to look next in the codebase

- `src/main/java` — engine implementation, rule interfaces, Spring Boot application and controllers
- `src/test/java` — tests demonstrating usage and expected behavior
- `pom.xml` — dependency and plugin configuration


## Troubleshooting

- Build errors: ensure Java and Maven versions match the pom properties.
- Test failures: run `mvn -e test` to see full stack traces. Use your IDE to debug failing tests.
- If an endpoint or API is missing, search the code for controller classes or unit tests to learn the expected routes and payloads.


---

If more specific README sections are desired (detailed API reference, sample rule definitions, architecture diagrams), provide a preference for rule format (Java classes, JSON/YAML, or spreadsheet) and the README will be expanded to include examples and reference docs.
