package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListNewsUseCaseTest {

    private final NewsRepository repository = mock(NewsRepository.class);
    private final ListNewsUseCase useCase = new ListNewsUseCase(repository);

    @Test
    void shouldReturnEmptyListWhenNoNews() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(useCase.execute().isEmpty());
    }

    @Test
    void shouldReturnAllNews() {
        List<News> newsList = List.of(
                new News(1L, "A", "conteudo A", LocalDateTime.now()),
                new News(2L, "B", "conteudo B", LocalDateTime.now())
        );
        when(repository.findAll()).thenReturn(newsList);

        assertEquals(2, useCase.execute().size());
        assertEquals(newsList, useCase.execute());
    }
}
