# 🏗️ Architecture

Stream Weaver simulates a high-throughput event pipeline using LocalStack to mirror AWS services.

## Event Flow

```mermaid
%%{init: {'flowchart': {'curve': 'basis'}}}%%
graph TD
    subgraph Clients
        User[User / curl]
    end

    subgraph Application ["Ingester Service (Spring Boot)"]
        direction TB
        API[REST Controller]
        Worker[Stream Consumer]
    end

    subgraph Infrastructure ["LocalStack (AWS)"]
        direction TB
        Kinesis[(Kinesis Stream)]
        Dynamo[(DynamoDB Tables)]
    end

    %% Flow
    User -- "1. POST JSON" --> API
    API -- "2. Publish Event" --> Kinesis
    Kinesis -- "3. Consume Batch" --> Worker
    Worker -- "4. Checkpoint / Lease" --> Dynamo
```

## Interaction Sequence

```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant P as Producer (API)
    participant K as Kinesis Stream
    participant C as Consumer (Worker)
    participant D as DynamoDB

    U->>P: POST /api/v1/events (JSON)
    P->>P: Wrap message in Event Record
    P->>K: PutRecord (StreamBridge)
    K-->>P: Acknowledgement
    P-->>U: 200 OK (Event ID)

    loop Async Consumption
        C->>K: GetRecords
        K-->>C: Event Batch
        C->>C: Process Event
        C->>D: Update Checkpoint/Lease
    end
```

## Infrastructure Resources

Defined in `infra/shared/main.tf`:

| Resource           | Name                   | Purpose                                                    |
|:-------------------|:-----------------------|:-----------------------------------------------------------|
| **Kinesis Stream** | `stream-weaver-events` | Buffers incoming events from the producer API.             |
| **DynamoDB Table** | `StreamWeaverMetadata` | Stores checkpoints for the Kinesis consumer binder.        |
| **DynamoDB Table** | `StreamWeaverLocks`    | Manages distributed locks for consumer group coordination. |
