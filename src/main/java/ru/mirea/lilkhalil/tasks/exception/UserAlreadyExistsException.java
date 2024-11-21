package ru.mirea.lilkhalil.tasks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_EMAIL = "User with email=%s already exists!";

    public UserAlreadyExistsException(String email) {
        super(MESSAGE_EMAIL.formatted(email));
    }

}
