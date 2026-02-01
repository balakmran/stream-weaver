# 🧵 Stream Weaver Agent Context

This file provides context for Gemini CLI agents working on the Stream Weaver project.

## 🚀 Tech Stack
- **Runtime:** Java 25 (LTS), GraalVM Native Image
- **Frameworks:** Spring Boot 4.0.2, Spring Cloud AWS 4.0.0
- **Concurrency:** Project Loom (Virtual Threads enabled)
- **Messaging:** Spring Cloud Stream with Kinesis Binder
- **Data:** DynamoDB (used for checkpoints and distributed locks)
- **Infrastructure:** Terraform (OpenTofu) + LocalStack (via `tflocal`)
- **Build Tool:** Gradle 9.3.0 (Kotlin DSL) in a monorepo structure

## 🏛️ Architecture & Conventions
- **Monorepo:** Shared models reside in `libs/common-models`. Service-specific logic is in `services/`.
- **Event-Driven:** Follow the Producer-Consumer pattern. The `ingester-service` acts as both.
- **Native-First:** Code must be compatible with GraalVM AOT. Avoid reflection where possible; use Spring's AOT-friendly annotations.
- **Virtual Threads:** High concurrency is expected. Avoid blocking operations on platform threads.

## 🛠️ Development Workflow
- **Infrastructure:** Use `localstack start -d` and `tflocal apply` within `infra/shared`.
- **JVM Mode (Fast):** Run via `./gradlew :services:ingester-service:bootRun --args='--spring.profiles.active=local'`.
- **Native Mode:** Build via `bootBuildImage` to verify AOT compatibility.

## 📖 Documentation
- Architecture deep-dives: `docs/architecture.md`
- Dev setup: `CONTRIBUTING.md`

## 📄 Java Style Guide
The project adheres to the Google Java Style Guide. Refer to the official guide for details:
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
