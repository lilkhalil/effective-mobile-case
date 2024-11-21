package ru.mirea.lilkhalil.tasks.service;

import ru.mirea.lilkhalil.tasks.dto.JwtDto;
import ru.mirea.lilkhalil.tasks.dto.SignInRqDto;
import ru.mirea.lilkhalil.tasks.dto.SignUpRqDto;
import ru.mirea.lilkhalil.tasks.exception.UserAlreadyExistsException;

/**
 * Сервис для управления процессами аутентификации и регистрации пользователей.
 */
public interface AuthenticationService {

    /**
     * Регистрация нового пользователя.
     *
     * @param signUpRqDto объект, содержащий данные для регистрации пользователя:
     *                    электронную почту, пароль и другие необходимые данные.
     * @return объект {@link JwtDto}, содержащий токен доступа, сгенерированный для нового пользователя.
     * @throws UserAlreadyExistsException если пользователь с указанным email уже существует.
     */
    JwtDto signUp(SignUpRqDto signUpRqDto) throws UserAlreadyExistsException;

    /**
     * Вход пользователя в систему.
     *
     * @param signInRqDto объект, содержащий данные для входа: имя пользователя или email и пароль.
     * @return объект {@link JwtDto}, содержащий токен доступ, сгенерированный после успешной аутентификации.
     */
    JwtDto signIn(SignInRqDto signInRqDto);
}
