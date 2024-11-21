package ru.mirea.lilkhalil.tasks.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.service.UserService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskPermissionServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskPermissionService permissionService;

    private User assignee;
    private User admin;
    private Task task;

    @BeforeEach
    void tearUp() {
        assignee = User.builder()
                .role(User.Role.ROLE_USER)
                .build();

        admin = User.builder()
                .role(User.Role.ROLE_ADMIN)
                .build();

        task = Task.builder()
                .assignee(assignee)
                .build();
    }

    @Test
    void isPermitted_shouldReturnTrue_whenAuthenticatedUserIsAdmin() {
        when(userService.getAuthenticatedUser()).thenReturn(admin);

        boolean result = permissionService.isPermitted(task);

        assertThat(result).isTrue();
    }

    @Test
    void isPermitted_shouldReturnTrue_whenAuthenticatedUserIsAssignee() {
        when(userService.getAuthenticatedUser()).thenReturn(assignee);

        boolean result = permissionService.isPermitted(task);

        assertThat(result).isTrue();
    }

    @Test
    void isPermitted_shouldReturnFalse_whenAuthenticatedUserIsNotAdminAndNotAssignee() {
        when(userService.getAuthenticatedUser()).thenReturn(new User());

        boolean result = permissionService.isPermitted(task);

        assertThat(result).isFalse();
    }

}