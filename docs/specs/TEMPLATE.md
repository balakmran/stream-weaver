# 📋 Feature Spec: [Feature Name]

**Status:** [Draft | Ready | In Progress | Done]
**Priority:** [High | Medium | Low]

## 🧠 AI Context
*Read these files before starting to understand the current state:*
- `docs/architecture.md`
- `services/.../TargetFile.java`
- `infra/.../main.tf`

## 🎯 Objective
*A single sentence describing the goal.*
(Example: "Persist consumed events to DynamoDB using the AWS SDK v2.")

## 🛠️ Technical Implementation

### 1. Infrastructure Changes
*   **File:** `infra/shared/main.tf`
*   **Action:** Add/Update resource `aws_dynamodb_table`.
*   **Details:**
    *   Name: `TableName`
    *   Key: `id` (String)

### 2. Code Changes
*   **File:** `services/ingester-service/.../Consumer.java`
*   **Action:** Inject Repository, call save.
*   **Constraints:**
    *   Use `DynamoDbEnhancedClient` (Native compatible).
    *   Do NOT use Reflection-heavy libraries if avoidable.
    *   Follow Google Java Style (2 spaces).

### 3. Verification Plan
*   **Automated:** Run `b./gradlew test` (specifically `IngesterServiceApplicationTests`).
*   **Manual:**
    1.  `curl -X POST ...`
    2.  `awslocal dynamodb scan ...`

## ✅ Definition of Done
- [ ] Infrastructure provisioned via `tflocal`.
- [ ] Code compiles and passes style check.
- [ ] E2E Test passes (Producer -> Kinesis -> Consumer -> DynamoDB).
