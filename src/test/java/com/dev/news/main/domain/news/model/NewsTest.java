package com.dev.news.main.domain.news.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewsTest {

    @Test
    void shouldCreateValidNews() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 26, 10, 0);
        News news = new News(1L, "Titulo valido", "Conteudo valido", createdAt);

        assertEquals(1L, news.id());
        assertEquals("Titulo valido", news.title());
        assertEquals("Conteudo valido", news.content());
        assertEquals(createdAt, news.createdAt());
    }

    @Test
    void shouldRejectNullTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> new News(null, null, "conteudo", LocalDateTime.now()));
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> new News(null, "   ", "conteudo", LocalDateTime.now()));
    }

    @Test
    void shouldRejectNullContent() {
        assertThrows(IllegalArgumentException.class,
                () -> new News(null, "titulo", null, LocalDateTime.now()));
    }

    @Test
    void shouldRejectBlankContent() {
        assertThrows(IllegalArgumentException.class,
                () -> new News(null, "titulo", "", LocalDateTime.now()));
    }
}
