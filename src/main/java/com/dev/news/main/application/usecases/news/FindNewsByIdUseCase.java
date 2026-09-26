package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.exceptions.NewsNotFoundException;
import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;

public record FindNewsByIdUseCase(NewsRepository newsRepository) {

    public News execute(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));
    }
}