package com.dev.news.main.infrastructure.adapters.out.persistence;

import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NewsRepositoryAdapter implements NewsRepository {

    private final SpringDataNewsRepository springDataRepository;

    public NewsRepositoryAdapter(SpringDataNewsRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public List<News> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<News> findById(Long id) {
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public News save(News news) {
        NewsEntity entity = toEntity(news);
        NewsEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    private News toDomain(NewsEntity entity) {
        return new News(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    private NewsEntity toEntity(News domain) {
        return new NewsEntity(
                domain.id(),
                domain.title(),
                domain.content(),
                domain.createdAt()
        );
    }
}
