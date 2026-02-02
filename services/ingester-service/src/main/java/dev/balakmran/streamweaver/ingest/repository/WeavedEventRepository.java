package dev.balakmran.streamweaver.ingest.repository;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class WeavedEventRepository {
  private final DynamoDbTable<WeavedEventEntity> table;

  public WeavedEventRepository(DynamoDbEnhancedClient enhancedClient) {
    this.table =
        enhancedClient.table("WeavedEvents", TableSchema.fromBean(WeavedEventEntity.class));
  }

  public void save(WeavedEventEntity entity) {
    table.putItem(entity);
  }
}