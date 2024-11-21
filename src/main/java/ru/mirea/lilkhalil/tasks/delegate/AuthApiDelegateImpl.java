package ru.mirea.lilkhalil.tasks.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.mirea.lilkhalil.tasks.api.AuthApiDelegate;
import ru.mirea.lilkhalil.tasks.dto.JwtDto;
import ru.mirea.lilkhalil.tasks.dto.SignInRqDto;
import ru.mirea.lilkhalil.tasks.dto.SignUpRqDto;
import ru.mirea.lilkhalil.tasks.service.AuthenticationService;

@Component
@RequiredArgsConstructor
public class AuthApiDelegateImpl implements AuthApiDelegate {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<JwtDto> signIn(SignInRqDto signInRqDto) {
        return ResponseEntity.ok(authenticationService.signIn(signInRqDto));
    }

    @Override
    public ResponseEntity<JwtDto> signUp(SignUpRqDto signUpRqDto) {
        return ResponseEntity.ok(authenticationService.signUp(signUpRqDto));
    }
}
