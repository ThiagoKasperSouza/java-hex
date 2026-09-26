package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.exceptions.NewsNotFoundException;
import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;

public record UpdateNewsUseCase(NewsRepository newsRepository) {

    public News execute(Long id, String title, String content) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));

        News updated = new News(existing.id(), title, content, existing.createdAt());
        return newsRepository.save(updated);
    }
}
