# 📋 Feature Spec: Event Persistence (Close the Loop)

**Status:** Ready
**Priority:** High

## 🧠 AI Context
*Read these files before starting:*
- `docs/architecture.md` (for the "WeavedEvents" table role)
- `infra/shared/main.tf` (where tables are defined)
- `services/ingester-service/src/main/java/dev/balakmran/streamweaver/ingest/consumer/EventConsumer.java` (logic to update)

## 🎯 Objective
Persist processed events from the Kinesis consumer into a new DynamoDB table (`WeavedEvents`) to enable data verification and "close the loop" of the pipeline.

## 🛠️ Technical Implementation

### 1. Infrastructure Changes
*   **File:** `infra/shared/main.tf`
*   **Action:** Create `aws_dynamodb_table` named `WeavedEvents`.
*   **Details:**
    *   Partition Key: `eventId` (String)
    *   Billing: PAY_PER_REQUEST

### 2. Data Access Layer
*   **Path:** `services/ingester-service/src/main/java/dev/balakmran/streamweaver/ingest/repository/`
*   **New File:** `WeavedEventEntity.java`
    *   Annotate with `@DynamoDbBean`.
    *   Fields: `eventId` (PK), `message`, `processedAt` (ISO String).
*   **New File:** `WeavedEventRepository.java`
    *   Wrap `DynamoDbEnhancedClient` to provide a `save(Event)` method.
*   **Configuration:** Ensure `DynamoDbClient` is available (Spring Cloud AWS usually provides this, but we may need to expose the enhanced client bean).

### 3. Consumer Logic
*   **File:** `services/ingester-service/src/main/java/dev/balakmran/streamweaver/ingest/consumer/EventConsumer.java`
*   **Action:**
    1.  Inject `WeavedEventRepository`.
    2.  Inside the lambda: Map `Event` record -> `WeavedEventEntity`.
    3.  Call `repository.save()`.
    4.  Log success *after* persistence.

### 4. Verification Plan
*   **Test File:** `services/ingester-service/src/test/java/dev/balakmran/streamweaver/ingest/IngesterServiceApplicationTests.java`
*   **Action:** Update test to assert that the item exists in DynamoDB after the event is published.
    *   Use `Awaitility` to wait for async processing.
    *   Use `DynamoDbClient` in the test to `getItem`.

## ✅ Definition of Done
- [ ] `tflocal apply` runs successfully with new table.
- [ ] `EventConsumer` saves data to DynamoDB.
- [ ] `IngesterServiceApplicationTests` passes (Green).
