package ru.mirea.lilkhalil.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.lilkhalil.tasks.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
