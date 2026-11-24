# Repository Guidelines

## Project Structure & Module Organization

This repository is an early Java booking-service scaffold. Production code lives in `src/main/java/com/cts/booking/`; `Main.java` is the current entry point. The `modules/` tree groups `catalog`, `inventory`, `ordering`, and `ticketing`, each with `domain`, `application`, and `infrastructure` directories. Keep business rules in domain code, use cases in application code, and REST, persistence, and external-service adapters in infrastructure.

Place tests under matching packages in `src/test/java/`. Use `src/main/resources/` and `src/test/resources/` for runtime and test resources. Requirements, architecture, process flows, and API designs live in `docs/`. These documents describe planned capabilities; the build currently applies only Gradle's Java plugin.

## Build, Test, and Development Commands

Use the checked-in Gradle wrapper (`gradlew.bat` on Windows):

- `./gradlew build` compiles, runs tests, and produces the JAR under `build/libs/`.
- `./gradlew test` runs the JUnit test suite.
- `./gradlew clean build` verifies a fresh build.
- `./gradlew test --tests 'com.cts.booking.modules.catalog.ExampleTest'` runs a selected test class once added.

Use JDK 25 for the current `Main.java` syntax. No Java toolchain is pinned in `build.gradle`. Run `com.cts.booking.Main` from the IDE for local execution; no Gradle `run` or `bootRun` task is configured.

## Coding Style & Naming Conventions

Match existing Java files: four-space indentation, opening braces on the declaration line, `UpperCamelCase` types, `lowerCamelCase` methods and fields, and `UPPER_SNAKE_CASE` enum values and constants. Use lowercase package names under `com.cts.booking`. Keep domain code independent of framework and infrastructure dependencies. No formatter or linter is currently configured.

## Testing Guidelines

The build configures JUnit Jupiter through the JUnit 6.0.0 BOM and JUnit Platform. Test directories currently contain no test classes, and no coverage threshold is enforced. Name test classes `*Test`, mirror production packages, and cover business rules, failure cases, and boundary conditions. Run `./gradlew test` before submitting code changes.

## Commit & Pull Request Guidelines

Existing commits use short descriptions such as `Init service` and `update base structure`; no formal commit convention is established. Write concise, action-oriented subjects and keep commits focused. Pull requests should explain the behavior changed, link relevant issues or design documents, and record validation commands and results. Update affected documentation when implementation changes an API or architectural decision.
