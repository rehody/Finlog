package org.example.finlog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class TestPostgresContainerConfig {

    static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16.9")
            .withDatabaseName("finlogtest")
            .withUsername("test")
            .withPassword("test");

    static {
        container.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}
