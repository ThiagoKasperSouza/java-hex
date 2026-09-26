package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;

import java.time.LocalDateTime;

public record CreateNewsUseCase(NewsRepository newsRepository) {

    public News execute(String title, String content) {
        // A própria record News aplica as validações de regra de negócio
        News novaNoticia = new News(
                null,
                title,
                content,
                LocalDateTime.now()
        );

        return newsRepository.save(novaNoticia);
    }
}