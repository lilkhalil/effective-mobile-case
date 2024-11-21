package ru.mirea.lilkhalil.tasks.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.exception.UserNotFoundException;
import ru.mirea.lilkhalil.tasks.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void tearUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("test@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getAuthenticatedUser_shouldReturnAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        User result = userService.getAuthenticatedUser();

        assertThat(result).isNotNull()
                .isEqualTo(testUser);
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }
}