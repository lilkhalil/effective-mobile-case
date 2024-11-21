package ru.mirea.lilkhalil.tasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Task with id=%d is not found!";

    public TaskNotFoundException(Long taskId) {
        super(MESSAGE.formatted(taskId));
    }
}
