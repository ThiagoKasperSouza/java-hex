package com.dev.news.main;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de integração ponta a ponta usando PostgreSQL real via Testcontainers.
 * O data.sql é executado (spring.sql.init.mode=always), validando também a
 * criação da tabela e o seed de 100 notícias.
 *
 * O Spring Boot 4 removeu TestRestTemplate/AutoConfigureMockMvc, por isso o
 * teste consome a API sobre HTTP real com o HttpClient do JDK e Jackson 3.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("db_news")
            .withUsername("postgres")
            .withPassword("postgres");

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpResponse<String> get(String path) throws Exception {
        return httpClient.send(
                HttpRequest.newBuilder(URI.create(baseUrl() + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        return httpClient.send(
                HttpRequest.newBuilder(URI.create(baseUrl() + path))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String path) throws Exception {
        return httpClient.send(
                HttpRequest.newBuilder(URI.create(baseUrl() + path)).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void contextLoads() {
        assertNotNull(objectMapper);
    }

    @Test
    void shouldListSeededNews() throws Exception {
        HttpResponse<String> resp = get("/api/news");

        assertEquals(200, resp.statusCode());
        JsonNode body = objectMapper.readTree(resp.body());
        assertTrue(body.isArray());
        assertTrue(body.size() > 0);
    }

    @Test
    void shouldFindSeededNewsById() throws Exception {
        HttpResponse<String> resp = get("/api/news/1");

        assertEquals(200, resp.statusCode());
        JsonNode body = objectMapper.readTree(resp.body());
        assertEquals(1L, body.get("id").asLong());
    }

    @Test
    void shouldReturn404ForUnknownId() throws Exception {
        HttpResponse<String> resp = get("/api/news/999999");

        assertEquals(404, resp.statusCode());
        JsonNode body = objectMapper.readTree(resp.body());
        assertEquals("Not Found", body.get("title").asText());
    }

    @Test
    void shouldCreateReadAndDeleteNews() throws Exception {
        // create
        HttpResponse<String> createResp = post("/api/news",
                "{\"title\":\"Notícia de teste\",\"content\":\"Conteúdo de teste.\"}");

        assertEquals(201, createResp.statusCode());
        JsonNode created = objectMapper.readTree(createResp.body());
        long id = created.get("id").asLong();
        assertEquals("Notícia de teste", created.get("title").asText());

        // read
        HttpResponse<String> getResp = get("/api/news/" + id);
        assertEquals(200, getResp.statusCode());

        // delete
        HttpResponse<String> delResp = delete("/api/news/" + id);
        assertEquals(204, delResp.statusCode());

        // read again -> 404
        HttpResponse<String> getAfter = get("/api/news/" + id);
        assertEquals(404, getAfter.statusCode());
    }

    @Test
    void shouldRejectBlankTitle() throws Exception {
        HttpResponse<String> resp = post("/api/news", "{\"title\":\"\",\"content\":\"x\"}");

        assertEquals(400, resp.statusCode());
        JsonNode body = objectMapper.readTree(resp.body());
        assertEquals("O título da notícia não pode ser vazio.", body.get("detail").asText());
    }
}
