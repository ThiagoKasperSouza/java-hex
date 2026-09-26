package com.dev.news.main.application.usecases.news;

import com.dev.news.main.domain.news.exceptions.NewsNotFoundException;
import com.dev.news.main.domain.news.model.News;
import com.dev.news.main.domain.news.ports.NewsRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteNewsUseCaseTest {

    private final NewsRepository repository = mock(NewsRepository.class);
    private final DeleteNewsUseCase useCase = new DeleteNewsUseCase(repository);

    @Test
    void shouldDeleteWhenNewsExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(
                new News(1L, "Titulo", "Conteudo", LocalDateTime.now())));

        useCase.execute(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenNewsDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> useCase.execute(1L));

        verify(repository, never()).deleteById(anyLong());
    }
}
