package ru.mirea.lilkhalil.tasks.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.service.PermissionService;
import ru.mirea.lilkhalil.tasks.service.UserService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskPermissionService implements PermissionService<Task> {

    private final UserService userService;

    @Override
    public boolean isPermitted(Task task) {
        User authenticated = userService.getAuthenticatedUser();
        User assignee = task.getAssignee();

        return isAdmin(authenticated) || Objects.equals(authenticated, assignee);
    }

    private boolean isAdmin(User user) {
        return Objects.equals(user.getRole(), User.Role.ROLE_ADMIN);
    }
}
