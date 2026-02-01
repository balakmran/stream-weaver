package dev.balakmran.streamweaver.ingest.api;

import dev.balakmran.streamweaver.models.Event;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
public class ProducerController {
  private static final Logger log = LoggerFactory.getLogger(ProducerController.class);
  private final StreamBridge streamBridge;
  private final String streamName;

  public ProducerController(
      StreamBridge streamBridge, @Value("${app.stream.destination}") String streamName) {
    this.streamBridge = streamBridge;
    this.streamName = streamName;
  }

  @PostMapping
  public String publish(@RequestBody PublishRequest request) {
    String id = UUID.randomUUID().toString();
    Event event = new Event(id, request.message(), Instant.now());

    log.info("🚀 Publishing Event ID: {}", id);

    streamBridge.send(streamName, event);

    return "Published: " + id;
  }
}
