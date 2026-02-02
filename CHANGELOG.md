# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.1] - 2026-02-01

### Added
- **Core Streaming Pipeline:** Full E2E flow from REST API -> Kinesis Data Stream -> Async Consumer -> DynamoDB Persistence.
- **Event Persistence:** Implemented `WeavedEventRepository` using AWS SDK v2 DynamoDB Enhanced Client for native-friendly storage in `WeavedEvents` table.
- **Infrastructure-as-Code:** Terraform (OpenTofu) configuration for Kinesis Streams and DynamoDB tables (`StreamWeaverCheckpoints`, `StreamWeaverLocks`, `WeavedEvents`).
- **High Performance Runtime:** Optimized for Java 25, Spring Boot 4, and Project Loom (Virtual Threads).
- **Native Image Support:** Full GraalVM AOT compatibility for sub-100ms startup times.
- **Monorepo Architecture:** Gradle-based management of shared records (`libs/common-models`) and microservices (`services/ingester-service`).
- **Developer Experience:** 
    - Comprehensive documentation (Architecture, Development Guide).
    - AI-friendly project context (`GEMINI.md`) and feature specs.
    - Local cloud simulation via LocalStack and OrbStack.
    - Automated E2E testing using Testcontainers and Test-Specific Terraform provisioning.
- **Style Consistency:** Adherence to Google Java Style Guide (2-space indentation) project-wide.
