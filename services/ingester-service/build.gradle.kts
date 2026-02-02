plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.graalvm.buildtools.native")
}

group = "dev.balakmran"
version = "0.0.1"
description = "ingester-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Internal & Cloud
    implementation(project(":libs:common-models"))
    implementation("io.awspring.cloud:spring-cloud-aws-kinesis-stream-binder")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-dynamodb")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Tooling
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "com.vaadin.external.google", module = "android-json")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:localstack:1.21.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.0")
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:4.0.0-RC1")
        mavenBom("org.testcontainers:testcontainers-bom:2.0.3")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    systemProperty("spring.profiles.active", "local")
    systemProperty("spring.devtools.restart.enabled", "false")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    // 1. Enable Native Compilation inside the buildpack
    environment.put("BP_NATIVE_IMAGE", "true")
    environment.put("BP_JVM_VERSION", "25")

    // 2. Whitelist all noisy logging and utility packages
    environment.put(
        "BP_NATIVE_IMAGE_BUILD_ARGUMENTS",
        listOf(
            // 1. Google Guava (The root cause of map issues)
            "--initialize-at-build-time=com.google.common",

            // 2. AWS Internals (Shadowed Jackson)
            "--initialize-at-build-time=software.amazon.awssdk.thirdparty.jackson",

            // 3. The "Logging Soup" (Whitelist ALL of them)
            "--initialize-at-build-time=org.slf4j",
            "--initialize-at-build-time=ch.qos.logback",
            "--initialize-at-build-time=org.apache.commons.logging",
            "--initialize-at-build-time=org.apache.logging.log4j",
            "--initialize-at-build-time=org.apache.logging.slf4j",
            "--initialize-at-build-time=org.springframework.boot.logging.log4j2",

            // 4. Include the Joda timezone files
            "--initialize-at-build-time=org.joda",
            "--initialize-at-build-time=tools.jackson",         // Spring Boot 4 default
            "--initialize-at-build-time=com.fasterxml.jackson"  // Legacy/AWS default
        ).joinToString(" ")
    )
}