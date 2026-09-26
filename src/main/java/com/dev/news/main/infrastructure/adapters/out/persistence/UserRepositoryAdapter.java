package com.dev.news.main.infrastructure.adapters.out.persistence;

import com.dev.news.main.domain.user.model.User;
import com.dev.news.main.domain.user.ports.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataRepository;

    public UserRepositoryAdapter(SpringDataUserRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return toDomain(springDataRepository.save(toEntity(user)));
    }

    private User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRole());
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(user.id(), user.username(), user.password(), user.role(), LocalDateTime.now());
    }
}
