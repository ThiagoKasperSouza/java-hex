package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.exceptions.NewsNotFoundException;
import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindNewsByIdUseCaseTest {

    private final NewsRepository repository = mock(NewsRepository.class);
    private final FindNewsByIdUseCase useCase = new FindNewsByIdUseCase(repository);

    @Test
    void shouldReturnNewsWhenFound() {
        News news = new News(1L, "Titulo", "Conteudo", LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(news));

        News result = useCase.execute(1L);

        assertEquals(news, result);
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> useCase.execute(99L));
    }
}
