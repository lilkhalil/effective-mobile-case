package ru.mirea.lilkhalil.tasks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.lilkhalil.tasks.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t " +
            "WHERE (:authorId IS NULL OR t.author.id = :authorId) " +
            "AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)")
    Page<Task> findByAuthorIdAndAssigneeId(
            @Param("authorId") Long authorId,
            @Param("assigneeId") Long assigneeId,
            Pageable pageable);
}
