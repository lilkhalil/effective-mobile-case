package ru.mirea.lilkhalil.tasks.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.dto.JwtDto;
import ru.mirea.lilkhalil.tasks.dto.SignInRqDto;
import ru.mirea.lilkhalil.tasks.dto.SignUpRqDto;
import ru.mirea.lilkhalil.tasks.exception.UserAlreadyExistsException;
import ru.mirea.lilkhalil.tasks.mapper.UserMapper;
import ru.mirea.lilkhalil.tasks.mapper.UserMapperImpl;
import ru.mirea.lilkhalil.tasks.repository.UserRepository;
import ru.mirea.lilkhalil.tasks.service.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private SignUpRqDto signUpRqDto;
    private SignInRqDto signInRqDto;
    private User testUser;

    @BeforeEach
    void tearUp() {
        signUpRqDto = SignUpRqDto.builder()
                .email("test@example.com")
                .password("password")
                .build();

        signInRqDto = SignInRqDto.builder()
                .email("test@example.com")
                .password("password")
                .build();

        testUser = User.builder()
                .email("test@example.com")
                .role(User.Role.ROLE_USER)
                .password("encodedPassword")
                .build();
    }

    @Test
    void signUp_shouldCreateNewUserAndReturnJwtToken() {
        when(userRepository.findByEmail(signUpRqDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRqDto.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        JwtDto result = authenticationService.signUp(signUpRqDto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwtToken");
        verify(userRepository).findByEmail(signUpRqDto.getEmail());
        verify(userMapper).signUpRqDtoToUser(signUpRqDto);
        verify(passwordEncoder).encode(signUpRqDto.getPassword());
        verify(userRepository).save(testUser);
        verify(jwtService).generateToken(testUser);
    }

    @Test
    void signUp_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.findByEmail(signUpRqDto.getEmail())).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authenticationService.signUp(signUpRqDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining(signUpRqDto.getEmail());
        verify(userRepository).findByEmail(signUpRqDto.getEmail());
        verifyNoInteractions(userMapper, passwordEncoder, jwtService);
    }

    @Test
    void signIn_shouldAuthenticateAndReturnJwtToken() {
        when(userDetailsService.loadUserByUsername(signInRqDto.getEmail())).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        JwtDto result = authenticationService.signIn(signInRqDto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwtToken");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(signInRqDto.getEmail());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    void signIn_shouldFailAuthentication_whenInvalidCredentials() {
        doThrow(BadCredentialsException.class).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authenticationService.signIn(signInRqDto))
                .isInstanceOf(AuthenticationException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService, jwtService);
    }
}