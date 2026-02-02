package dev.balakmran.streamweaver.ingest.consumer;

import dev.balakmran.streamweaver.ingest.repository.WeavedEventEntity;
import dev.balakmran.streamweaver.ingest.repository.WeavedEventRepository;
import dev.balakmran.streamweaver.models.Event;
import java.time.Instant;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConsumer {
  private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);
  private final WeavedEventRepository repository;

  public EventConsumer(WeavedEventRepository repository) {
    this.repository = repository;
  }

  @Bean
  public Consumer<Event> processEvent() {
    return event -> {
      log.info(
          "🧵 [Consuming] Thread: '{}' | Received: {}", Thread.currentThread().getName(), event);

      WeavedEventEntity entity = new WeavedEventEntity();
      entity.setEventId(event.id());
      entity.setMessage(event.message());
      entity.setProcessedAt(Instant.now().toString());

      repository.save(entity);

      log.info("✅ Event persisted to DynamoDB: {}", event.id());
    };
  }
}
