# Generic Rule Engine

A lightweight, generic rule engine built with Java 8 and Spring Boot that loads rules from Excel spreadsheets and evaluates them using Java 8 Predicates.

Key features
- Loads rules from an Excel (.xlsx) file (Apache POI) and keeps them in memory.
- Converts rule rows into Rule objects and composes Java Predicate<Rule> from a RuleContext.
- Provides a REST endpoint to evaluate a request context and return aggregated suggestions.
- Simple architecture: Loader -> Predicate Creator -> Service -> Aggregator.

Technologies
- Java 1.8
- Spring Boot 2.1.6.RELEASE
- Apache POI (poi, poi-ooxml)
- Maven

Quickstart
1. Configure the path to your rules Excel file (see Configuration).
2. Build:
   mvn clean package
3. Run:
   mvn spring-boot:run

Configuration
- cloud.advisory.rule.loader.file.path (application.properties)
  - This property must point to the Excel file containing rules. Example:
    cloud.advisory.rule.loader.file.path=/path/to/input-file.xlsx

API
- POST /rules/execute
  - Request body: a JSON RuleContext (see resources/request.json for an example)
  - Response: JSON array of suggestion strings (List<String>) aggregated from matched rules

Rule file format (Excel)
- First row: column headers. Expected special headers:
  - "Rule-Id" column for rule identifier
  - Columns containing the word "Assessment" or similar are treated as suggestion columns
  - Other columns are treated as regular columns used for matching
- Each subsequent row describes one rule. The loader maps cell values into Columns and Rules.

How it works (high-level)
1. RulesLoader reads the configured Excel file at startup and converts each row into a Rule object.
2. The PredicatesCreator (DirectPredicatesCreator) converts an incoming RuleContext into a Predicate<Rule> that tests whether a rule matches the provided context.
3. RuleEngineService filters the loaded rules with the predicate and passes matched rules to ResponseAggregator for suggestions aggregation.
4. The controller exposes POST /rules/execute to accept contexts and return aggregated suggestions.

Example request
- See src/main/resources/request.json for a sample RuleContext payload.

Developer notes
- The code uses a simple direct predicate creator; more complex predicate strategies (e.g., inverse or composite rules) can be added under predicate.creator.
- ExcelFileReader expects string cell values for most columns; some cell type handling is implemented.

Testing
- Run unit tests with:
  mvn test

Contributing
- Fork the repo, create a feature branch, add tests, and open a pull request.

License
- Apache License 2.0 (see LICENSE file)

Contact
- For questions or improvements, open an issue or submit a pull request.
