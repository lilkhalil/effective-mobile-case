package ru.mirea.lilkhalil.tasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private static final String MESSAGE_ID = "User with id=%d is not found!";
    private static final String MESSAGE_EMAIL = "User with email=%s is not found!";

    public UserNotFoundException(Long userId) {
        super(MESSAGE_ID.formatted(userId));
    }

    public UserNotFoundException(String email) {
        super(MESSAGE_EMAIL.formatted(email));
    }
}
