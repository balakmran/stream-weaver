# 🤝 Contributing to Stream Weaver

We welcome contributions! Here is how to run the project locally for development without waiting for Native Image builds.

## ⚡ Fast Development Loop (JVM Mode)

For rapid iteration, run the application on the HotSpot JVM instead of compiling a native image.

### 1. Start Infrastructure
Ensure LocalStack is running and resources are provisioned (see [README.md](README.md#1-start-infrastructure)).

```bash
localstack start -d
cd infra/shared && tflocal apply --auto-approve
```

### 2. Run the Service
Use the `local` profile to connect to LocalStack from the host machine.

```bash
# From the project root
./gradlew :services:ingester-service:bootRun --args='--spring.profiles.active=local'
```

*The application will start on port `8080`.*

## 🧪 Running Tests

Run the integration tests (requires Docker for Testcontainers):

```bash
./gradlew test
```

## 🧹 Code Style

This project uses standard Java coding conventions. Please ensure your code formats cleanly before submitting a PR.
