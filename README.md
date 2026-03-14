# Generic Rule Engine

A lightweight, extensible rule engine built with Spring Boot.

Overview

This repository contains a modular rule evaluation engine that can load, manage and evaluate business rules against a set of input facts. It is designed to be a starting point for systems that require dynamic rule evaluation (validation, scoring, routing, feature flags, etc.).

Goals

- Provide a simple, extensible core for rule evaluation.
- Make rule definitions data-driven so non-developers can author rules.
- Offer REST endpoints for rule management and evaluation.

Key details

- Java: 1.8
- Framework: Spring Boot
- ArtifactId: rule-engine

Features

- Pluggable rule evaluators (support custom rule types and result mapping)
- REST-friendly Spring Boot skeleton for exposing rule APIs
- Optional import/export helpers (Excel support via Apache POI included)

Table of contents

- Quick start
- Building & running
- Configuration
- API examples
- Rule format examples
- Import / Export
- Extending the engine
- Testing
- Contributing
- License & support

Quick start

Prerequisites

- Java 8 (JDK 1.8)
- Maven (or use the included Maven wrapper: ./mvnw)

Build

1. Clone the repository:
   git clone <repo-url>
   cd generic-rule-engine

2. Build with Maven (wrapper provided):
   ./mvnw clean package

Run locally

- Run via Maven:
  ./mvnw spring-boot:run

- Or run the packaged jar:
  java -jar target/*-SNAPSHOT.jar

By default the application binds to port 8080 (configurable via application.properties).

Configuration

Configuration is standard Spring Boot (src/main/resources/application.properties or application.yml). Important properties to consider:

- server.port — HTTP port for the application
- rule.storage.* — (placeholder) add properties if you wire database-backed rule storage

API examples

Note: the concrete endpoints and request/response shapes are implemented in the source. The examples below are typical and intended to match the code in this repo; adapt them if the controller mappings differ.

1) Create / upload rules

POST /rules
Content-Type: application/json

Request body (example):
{
  "id": "age-check",
  "description": "Require age >= 18",
  "type": "simple",
  "condition": "facts.customerAge >= 18",
  "actions": [ { "type": "set", "target": "eligible", "value": true } ]
}

Response: 201 Created with created rule metadata.

2) List rules

GET /rules
Response: JSON array of rule metadata

3) Evaluate rules

POST /evaluate
Content-Type: application/json

Request body (example):
{
  "facts": { "customerAge": 35, "country": "US", "orderValue": 120.5 },
  "ruleIds": ["age-check"]
}

Example response (per-rule results):
{
  "results": [
    {
      "ruleId": "age-check",
      "matched": true,
      "outputs": { "eligible": true }
    }
  ]
}

Rule format (recommended JSON shape)

A recommended, minimal rule JSON format used by this project:

{
  "id": "<unique-id>",
  "description": "Human readable description",
  "type": "simple|script|dsl|composite",
  "condition": "<expression or script>",
  "actions": [ { "type": "set", "target": "x", "value": 1 } ],
  "meta": { }
}

- condition: expression evaluated against the provided facts. Replace with a safe expression language (SpEL, MVEL, JS) if needed.
- actions: optional list of outputs or side-effects produced when the rule matches.

Import / Export

- Excel import/export helpers are provided as optional utilities (Apache POI is available in the pom). Use them to bulk-load rules from spreadsheets.
- Add serializers/deserializers to support other formats (YAML, DB rows, remote API).

Extending the engine

- Add a new evaluator: implement the evaluator interface (check src/main/java for the evaluator contract) and register the implementation as a Spring Bean.
- Add persistence: implement a RuleRepository and wire it into the RuleService to store rules in a database.
- Add a UI: build a separate front-end that calls the REST APIs for rule management and evaluation.

Testing

- Unit tests should live under src/test/java. Run tests with:
  ./mvnw test

- Add integration tests that start the Spring context and verify evaluation outcomes for example rule sets.

Contributing

Contributions are welcome. Suggested workflow:

1. Fork the repository
2. Create a feature branch (git checkout -b feature/your-feature)
3. Add tests for new behavior
4. Open a pull request with a clear description and rationale

Coding style: follow idiomatic Java and Spring Boot best practices. Keep changes focused and include tests.

License & support

This project includes a LICENSE file in the repository root. Review it to confirm the license terms.

Maintainer / Contact

- groupId: com.bss.app
- artifactId: rule-engine
- For questions, open an issue on the repository.

Acknowledgements

This README was generated and expanded to provide a clear developer-friendly introduction to the project. Review controllers and models in src/main/java for concrete API details and update examples here to match the exact shapes implemented.
