package com.dev.news.main.domain.news.exceptions;

public class NewsNotFoundException extends RuntimeException {
    public NewsNotFoundException(Long id) {
        super("Notícia não encontrada com o ID: " + id);
    }
}
