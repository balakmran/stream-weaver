package dev.balakmran.streamweaver.ingest.consumer;

import dev.balakmran.streamweaver.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventConsumer {
    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @Bean
    public Consumer<Event> processEvent() {
        return event -> {
            // Running on a Virtual Thread automatically!
            log.info("🧵 [Consuming] Thread: '{}' | Received: {}",
                    Thread.currentThread().getName(),
                    event);

            // TODO: Next step -> Save to DynamoDB
        };
    }
}