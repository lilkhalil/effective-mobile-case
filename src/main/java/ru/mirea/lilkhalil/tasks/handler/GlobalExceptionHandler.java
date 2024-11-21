package ru.mirea.lilkhalil.tasks.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mirea.lilkhalil.tasks.dto.ErrorDto;
import ru.mirea.lilkhalil.tasks.exception.TaskNotFoundException;
import ru.mirea.lilkhalil.tasks.exception.UserAlreadyExistsException;
import ru.mirea.lilkhalil.tasks.exception.UserNotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_MESSAGE = "Exception thrown: ";

    @ExceptionHandler({TaskNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(RuntimeException ex) {
        log.error(LOG_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDto.builder()
                        .message(ex.getMessage())
                        .code(HttpStatus.NOT_FOUND.value())
                        .build());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        log.error(LOG_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getBindingResult().getAllErrors().stream()
                                .map(error -> {
                                    if (error instanceof FieldError fieldError) {
                                        return String.format("Поле '%s': %s", fieldError.getField(), fieldError.getDefaultMessage());
                                    } else {
                                        return error.getDefaultMessage();
                                    }
                                })
                                .collect(Collectors.joining(", ")))
                        .build());
    }

    @ExceptionHandler({IllegalArgumentException.class, UserAlreadyExistsException.class})
    public ResponseEntity<ErrorDto> handleBadRequestException(RuntimeException ex) {
        log.error(LOG_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorDto.builder()
                        .message(ex.getMessage())
                        .code(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorDto> handleUnauthorizedException(AuthenticationException ex) {
        log.error(LOG_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorDto.builder()
                        .message(ex.getMessage())
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorDto> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(LOG_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorDto.builder()
                        .message(ex.getMessage())
                        .code(HttpStatus.FORBIDDEN.value())
                        .build());
    }
}
