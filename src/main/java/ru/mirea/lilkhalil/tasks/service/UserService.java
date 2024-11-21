package ru.mirea.lilkhalil.tasks.service;

import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.exception.UserNotFoundException;

/**
 * Сервис для управления данными пользователей.
 * Предоставляет методы для получения информации о пользователях и аутентифицированном пользователе.
 */
public interface UserService {

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return объект {@link User}, представляющий пользователя с указанным идентификатором.
     * @throws UserNotFoundException если пользователь с указанным идентификатором не найден.
     */
    User getUserById(Long userId) throws UserNotFoundException;

    /**
     * Возвращает пользователя по его электронной почте.
     *
     * @param email адрес электронной почты пользователя.
     * @return объект {@link User}, представляющий пользователя с указанной электронной почтой.
     * @throws UserNotFoundException если пользователь с указанной электронной почтой не найден.
     */
    User getUserByEmail(String email) throws UserNotFoundException;

    /**
     * Возвращает текущего аутентифицированного пользователя.
     *
     * @return объект {@link User}, представляющий текущего аутентифицированного пользователя.
     */
    User getAuthenticatedUser();
}
