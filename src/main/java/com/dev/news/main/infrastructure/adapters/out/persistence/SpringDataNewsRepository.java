package com.dev.news.main.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataNewsRepository extends JpaRepository<NewsEntity, Long> {
}
