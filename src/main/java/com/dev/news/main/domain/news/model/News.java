package com.dev.news.main.domain.news.model;
import java.time.LocalDateTime;

public record News (
    Long id,
    String title,
    String content,
    LocalDateTime createdAt
) {
    public News {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título da notícia não pode ser vazio.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da notícia não pode ser vazio.");
        }
    }
}
