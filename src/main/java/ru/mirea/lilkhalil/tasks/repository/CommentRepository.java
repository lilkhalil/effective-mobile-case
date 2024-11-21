package ru.mirea.lilkhalil.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.lilkhalil.tasks.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
