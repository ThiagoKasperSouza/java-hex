package com.dev.news.main.domain.news.ports;

import com.dev.news.main.domain.news.model.News;
import java.util.List;
import java.util.Optional;

public interface NewsRepository {
    Optional<News> findById(Long id);
    List<News> findAll();
    News save(News news);
    void deleteById(Long id);
}
