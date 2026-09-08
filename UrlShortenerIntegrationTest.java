package com.schwab.urlshortener.integration;

import com.schwab.urlshortener.dto.CreateShortUrlRequest;
import com.schwab.urlshortener.dto.ShortUrlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlShortenerIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired TestRestTemplate restTemplate;
    @LocalServerPort int port;

    @Test
    void createThenRedirect() {
        var request = new CreateShortUrlRequest("https://example.com/integration", null);
        var create = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/urls", request, ShortUrlResponse.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());
        assertNotNull(create.getBody());

        var redirect = restTemplate.getForEntity(
                "http://localhost:" + port + "/" + create.getBody().shortCode(), String.class);
        assertEquals(HttpStatus.FOUND, redirect.getStatusCode());
        assertEquals("https://example.com/integration", redirect.getHeaders().getLocation().toString());
    }
}
