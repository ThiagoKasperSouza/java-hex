package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.exceptions.NewsNotFoundException;
import com.dev.news.main.domain.news.ports.NewsRepository;

public record DeleteNewsUseCase(NewsRepository newsRepository) {

    public void execute(Long id) {
        // Valida se a notícia existe antes de deletar
        newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));

        newsRepository.deleteById(id);
    }
}
