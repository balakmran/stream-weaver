package dev.balakmran.streamweaver.ingest.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class WeavedEventEntity {
  private String eventId;
  private String message;
  private String processedAt;

  public WeavedEventEntity() {}

  @DynamoDbPartitionKey
  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(String processedAt) {
    this.processedAt = processedAt;
  }
}