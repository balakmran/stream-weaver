package dev.balakmran.streamweaver.ingest;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    Network network() {
        return Network.newNetwork();
    }

    @Bean
    LocalStackContainer localStackContainer(Network network) {
        LocalStackContainer container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.0.3"))
                .withNetwork(network)
                .withNetworkAliases("localstack")
                .withServices(LocalStackContainer.Service.KINESIS, LocalStackContainer.Service.DYNAMODB);
        
        // Start immediately to make connection details available for System properties
        container.start();

        System.setProperty("spring.cloud.aws.endpoint", container.getEndpoint().toString());
        System.setProperty("spring.cloud.aws.region.static", container.getRegion());
        System.setProperty("spring.cloud.aws.credentials.access-key", container.getAccessKey());
        System.setProperty("spring.cloud.aws.credentials.secret-key", container.getSecretKey());

        return container;
    }

    @Bean
    GenericContainer<?> terraformContainer(Network network, LocalStackContainer localStack) {
        // Assume execution from services/ingester-service
        String projectRoot = Paths.get("").toAbsolutePath().getParent().getParent().toString();
        String infraPath = Paths.get(projectRoot, "infra", "shared").toString();
        String overridePath = Paths.get("src", "test", "resources", "terraform", "provider_override.tf").toAbsolutePath().toString();

        return new GenericContainer<>(DockerImageName.parse("hashicorp/terraform:1.10"))
                .withNetwork(network)
                .dependsOn(localStack)
                .withCopyFileToContainer(MountableFile.forHostPath(infraPath), "/workspace")
                .withCopyFileToContainer(MountableFile.forHostPath(overridePath), "/workspace/override.tf")
                .withWorkingDirectory("/workspace")
                .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("/bin/sh"))
                .withCommand("-c", "terraform init && terraform apply -auto-approve")
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("terraform")))
                .withStartupCheckStrategy(
                        new OneShotStartupCheckStrategy().withTimeout(Duration.ofMinutes(5))
                );
    }
}
