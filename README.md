# Stream Weaver 🧵

**Stream Weaver** is a high-performance, event-driven streaming platform built to demonstrate the
capabilities of **Java 25**, **Spring Boot 4**, and **GraalVM Native Images**.

It simulates a robust AWS pipeline (Producer → Kinesis → Consumer → DynamoDB) running entirely on a
local machine using **LocalStack** and **OrbStack**, achieving sub-second startup times and minimal memory
footprint.

## 🏗️ Architecture

- **Monorepo Structure**: Gradle-based monorepo managing shared libraries and microservices.
- **Ingester Service**: A Spring Cloud Stream application acting as both a REST Producer and a Kinesis Consumer.
- **Runtime**: Compiled to a Native Linux Executable via GraalVM for instant startup (<50ms).
- **Infrastructure**: Managed via Terraform (OpenTofu) and deployed to LocalStack.

## ⚡ Tech Stack

| Component   | Technology            | Version         |
|-------------|-----------------------|-----------------|
| Language    | Java (Oracle GraalVM) | 25 (LTS)        |
| Framework   | Spring Boot           | 4.0.2           |
| Cloud       | Spring Cloud AWS      | 4.0.0           |
| Build Tool  | Gradle (Kotlin DSL)   | 9.3.0           |
| Concurrency | Project Loom          | Virtual Threads |
| IaC         | Terraform / OpenTofu  | 1.6+            |
| Local Cloud | LocalStack            | Latest          |

## 🛠️ Prerequisites

Ensure you have the following tools installed (optimized for macOS/Apple Silicon):

1. Ghostty / Terminal: Your command center.

2. SDKMAN!: For managing Java and Gradle.
```bash
sdk install java 25.0.2-graal
sdk install gradle 9.3.0
```

3. OrbStack: The faster alternative to Docker Desktop.

4. LocalStack Tools (managed via uv):
```bash
uv tool install awscli-local
uv tool install terraform-local
```

## 🚀 Quick Start Guide

1. Start Infrastructure

Launch LocalStack in the background (detached mode).

```shell
localstack start -d
```


2. Provision Resources

Use tflocal to deploy the Kinesis Stream (stream-weaver-events) and DynamoDB tables
(WeaverProcessedItems).

```shell
cd infra/shared
tflocal init
tflocal apply --auto-approve
```

3. Build Native Image

Compile the ingester-service into a native container. This uses Cloud Native Buildpacks to perform
AOT (Ahead-of-Time) compilation.

```shell
# From the project root
cd services/ingester-service
../../gradlew bootBuildImage --imageName=ingester-service:0.0.1-SNAPSHOT
```

☕ Grab a coffee—this takes ~2-3 minutes as GraalVM analyzes reachability.

4. Run the Application

Start the microservice using Docker Compose. This ensures the native Linux container can talk to 
the LocalStack instance on your Mac host.

```shell
# From the project root
docker-compose up
```

Success Indicator: Look for a startup time of <0.1 seconds.

## 🧪 Verification

1. Trigger the Flow

Send a REST request to the Ingester Service. It will wrap the message in a WeaverEvent record and
push it to Kinesis.

```shell
curl -X POST http://localhost:8080/api/v1/events/Hello-Native-World
```

2. Check Persistence

Verify that the consumer picked up the event from Kinesis and saved it to the local DynamoDB table.

```shell
awslocal dynamodb scan --table-name WeaverProcessedItems
```

## 📂 Project Structure

```text
stream-weaver/
├── docker-compose.yml       # Orchestrates the Native App
├── settings.gradle.kts      # Monorepo Module Definitions
├── infra/
│   └── shared/              # Terraform for Kinesis/DynamoDB
├── libs/
│   └── common-models/       # Shared Java 25 Records (DTOs)
└── services/
└── ingester-service/        # Spring Boot 4 Application
```