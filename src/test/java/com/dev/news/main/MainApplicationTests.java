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
 * Teste de integração ponta a ponta com PostgreSQL real (Testcontainers) e o
 * fluxo completo de autenticação JWT. O admin é criado automaticamente na
 * inicialização (AdminUserBootstrap com as credenciais padrão admin/admin123).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainApplicationTests {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

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

    private HttpResponse<String> send(HttpRequest.Builder builder) throws Exception {
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest.Builder getBuilder(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl() + path)).GET();
    }

    private HttpRequest.Builder postBuilder(String path, String json) {
        return HttpRequest.newBuilder(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
    }

    private HttpRequest.Builder putBuilder(String path, String json) {
        return HttpRequest.newBuilder(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));
    }

    private HttpRequest.Builder deleteBuilder(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl() + path)).DELETE();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    private String login(String username, String password) throws Exception {
        HttpResponse<String> resp = send(postBuilder("/login",
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"));
        assertEquals(200, resp.statusCode());
        return objectMapper.readTree(resp.body()).get("token").asText();
    }

    private String adminToken() throws Exception {
        return login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    @Test
    void contextLoads() {
        assertNotNull(objectMapper);
    }

    @Test
    void shouldRegisterUser() throws Exception {
        HttpResponse<String> resp = send(postBuilder("/user",
                "{\"username\":\"novo_usuario\",\"password\":\"senha123\"}"));

        assertEquals(201, resp.statusCode());
        JsonNode body = objectMapper.readTree(resp.body());
        assertEquals("novo_usuario", body.get("username").asText());
        assertEquals("USER", body.get("role").asText());
    }

    @Test
    void shouldRejectDuplicateUser() throws Exception {
        send(postBuilder("/user", "{\"username\":\"duplicado\",\"password\":\"senha123\"}"));

        HttpResponse<String> resp = send(postBuilder("/user",
                "{\"username\":\"duplicado\",\"password\":\"senha123\"}"));
        assertEquals(409, resp.statusCode());
    }

    @Test
    void shouldLoginAndSetHttpOnlyCookie() throws Exception {
        HttpResponse<String> resp = send(postBuilder("/login",
                "{\"username\":\"" + ADMIN_USERNAME + "\",\"password\":\"" + ADMIN_PASSWORD + "\"}"));

        assertEquals(200, resp.statusCode());
        JsonNode body = objectMapper.readTree(resp.body());
        assertEquals(ADMIN_USERNAME, body.get("username").asText());
        assertEquals("ADMIN", body.get("role").asText());

        String setCookie = resp.headers().firstValue("Set-Cookie").orElse("");
        assertTrue(setCookie.contains("news_token="));
        assertTrue(setCookie.contains("HttpOnly"));
    }

    @Test
    void shouldRejectInvalidLogin() throws Exception {
        HttpResponse<String> resp = send(postBuilder("/login",
                "{\"username\":\"admin\",\"password\":\"senha_errada\"}"));
        assertEquals(401, resp.statusCode());
    }

    @Test
    void shouldListNewsAuthenticated() throws Exception {
        String token = adminToken();

        HttpResponse<String> resp = send(getBuilder("/api/news").header("Authorization", bearer(token)));
        assertEquals(200, resp.statusCode());

        JsonNode body = objectMapper.readTree(resp.body());
        assertTrue(body.isArray());
        assertTrue(body.size() > 0);
    }

    @Test
    void shouldDenyUnauthenticatedRead() throws Exception {
        HttpResponse<String> resp = send(getBuilder("/api/news"));
        assertEquals(401, resp.statusCode());
    }

    @Test
    void shouldCreateUpdateAndDeleteNewsAsAdmin() throws Exception {
        String auth = bearer(adminToken());

        // create
        HttpResponse<String> createResp = send(postBuilder("/api/news",
                "{\"title\":\"Notícia admin\",\"content\":\"Conteúdo.\"}").header("Authorization", auth));
        assertEquals(201, createResp.statusCode());
        long id = objectMapper.readTree(createResp.body()).get("id").asLong();

        // update
        HttpResponse<String> updateResp = send(putBuilder("/api/news/" + id,
                "{\"title\":\"Notícia atualizada\",\"content\":\"Novo conteúdo.\"}").header("Authorization", auth));
        assertEquals(200, updateResp.statusCode());
        JsonNode updated = objectMapper.readTree(updateResp.body());
        assertEquals("Notícia atualizada", updated.get("title").asText());

        // get
        HttpResponse<String> getResp = send(getBuilder("/api/news/" + id).header("Authorization", auth));
        assertEquals(200, getResp.statusCode());

        // delete
        HttpResponse<String> delResp = send(deleteBuilder("/api/news/" + id).header("Authorization", auth));
        assertEquals(204, delResp.statusCode());

        // after delete -> 404
        HttpResponse<String> after = send(getBuilder("/api/news/" + id).header("Authorization", auth));
        assertEquals(404, after.statusCode());
    }

    @Test
    void shouldDenyNewsCreationForNonAdminUser() throws Exception {
        String username = "nao_admin_" + System.currentTimeMillis();
        send(postBuilder("/user", "{\"username\":\"" + username + "\",\"password\":\"senha123\"}"));

        String token = login(username, "senha123");
        HttpResponse<String> resp = send(postBuilder("/api/news",
                "{\"title\":\"x\",\"content\":\"y\"}").header("Authorization", bearer(token)));
        // Usuário autenticado (USER) mas sem a role ADMIN é negado.
        // Nesta versão do Spring Security, acesso negado também retorna 401.
        assertEquals(401, resp.statusCode());
    }

    @Test
    void shouldRejectBlankTitleAsAdmin() throws Exception {
        String auth = bearer(adminToken());

        HttpResponse<String> resp = send(postBuilder("/api/news",
                "{\"title\":\"\",\"content\":\"x\"}").header("Authorization", auth));
        assertEquals(400, resp.statusCode());
    }
}
