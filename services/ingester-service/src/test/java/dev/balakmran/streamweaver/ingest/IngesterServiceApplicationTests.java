package dev.balakmran.streamweaver.ingest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import dev.balakmran.streamweaver.ingest.api.PublishRequest;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IngesterServiceApplicationTests {

  @LocalServerPort private int port;

  @Autowired private DynamoDbClient dynamoDbClient;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void shouldProcessAndPersistEvent() {
    // 1. Publish Event via REST
    PublishRequest request = new PublishRequest("Test Message");

    String responseBody =
        restClient
            .post()
            .uri("/api/v1/events")
            .body(request)
            .retrieve()
            .body(String.class);

    assertThat(responseBody).contains("Published:");
    String eventId = responseBody.split(": ")[1];

    // 2. Verify Persistence in DynamoDB (Async)
    await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(500))
        .untilAsserted(
            () -> {
              GetItemResponse itemResponse =
                  dynamoDbClient.getItem(
                      GetItemRequest.builder()
                          .tableName("WeavedEvents")
                          .key(Map.of("eventId", AttributeValue.builder().s(eventId).build()))
                          .build());

              assertThat(itemResponse.hasItem()).isTrue();
              assertThat(itemResponse.item().get("message").s()).isEqualTo("Test Message");
            });
  }
}