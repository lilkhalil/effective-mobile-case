package ru.mirea.lilkhalil.tasks.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Сервис для работы с JWT (JSON Web Token).
 * Предоставляет методы для извлечения данных из токена, генерации токенов и проверки их валидности.
 */
public interface JwtService {

    /**
     * Извлекает имя пользователя (username) из переданного JWT.
     *
     * @param token строка, представляющая JWT.
     * @return имя пользователя, извлечённое из токена.
     */
    String extractUsername(String token);

    /**
     * Генерирует JWT на основе предоставленных данных пользователя.
     *
     * @param userDetails объект {@link UserDetails}, содержащий информацию о пользователе
     * @return строка, представляющая сгенерированный JWT.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Проверяет валидность токена для указанного пользователя.
     *
     * @param token строка, представляющая JWT.
     * @param userDetails объект {@link UserDetails}, содержащий данные пользователя
     * @return {@code true}, если токен валиден и соответствует указанному пользователю;
     *         {@code false} в противном случае.
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
