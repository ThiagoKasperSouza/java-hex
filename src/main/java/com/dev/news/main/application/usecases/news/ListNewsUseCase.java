package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;

import java.util.List;

public record ListNewsUseCase(NewsRepository newsRepository) {

    public List<News> execute() {
        return newsRepository.findAll();
    }
}