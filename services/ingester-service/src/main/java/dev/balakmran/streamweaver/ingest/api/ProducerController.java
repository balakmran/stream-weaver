package dev.balakmran.streamweaver.ingest.api;

import dev.balakmran.streamweaver.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class ProducerController {
    private static final Logger log = LoggerFactory.getLogger(ProducerController.class);
    private final StreamBridge streamBridge;

    public ProducerController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @PostMapping("/{message}")
    public String publish(@PathVariable String message) {
        String id = UUID.randomUUID().toString();
        Event event = new Event(id, message, Instant.now().toString());

        log.info("🚀 Publishing Event ID: {}", id);

        // "stream-weaver-events" matches your Kinesis stream name
        streamBridge.send("stream-weaver-events", event);

        return "Published: " + id;
    }
}