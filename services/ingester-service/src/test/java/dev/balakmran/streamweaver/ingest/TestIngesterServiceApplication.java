package dev.balakmran.streamweaver.ingest;

import org.springframework.boot.SpringApplication;

public class TestIngesterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(IngesterServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
