package ru.mirea.lilkhalil.tasks.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import ru.mirea.lilkhalil.tasks.domain.Comment;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.dto.*;
import ru.mirea.lilkhalil.tasks.exception.TaskNotFoundException;
import ru.mirea.lilkhalil.tasks.mapper.TaskMapper;
import ru.mirea.lilkhalil.tasks.repository.TaskRepository;
import ru.mirea.lilkhalil.tasks.service.CommentService;
import ru.mirea.lilkhalil.tasks.service.PermissionService;
import ru.mirea.lilkhalil.tasks.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @Mock
    private PermissionService<Task> permissionService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User admin;
    private User eligibleUser;
    private User ineligibleUser;
    private Task task;
    private TaskDto taskDto;
    private TaskRqDto taskRqDto;
    private Comment comment;
    private CommentRqDto commentRqDto;

    @BeforeEach
    void tearUp() {
        admin = User.builder()
                .id(1L)
                .email("admin@example.com")
                .role(User.Role.ROLE_ADMIN)
                .build();
        eligibleUser = User.builder()
                .id(2L)
                .email("eligible@example.com")
                .role(User.Role.ROLE_USER)
                .build();

        ineligibleUser = User.builder()
                .id(3L)
                .email("ineligible@example.com")
                .role(User.Role.ROLE_USER)
                .build();

        task = Task.builder()
                .id(1L)
                .author(admin)
                .assignee(eligibleUser)
                .build();

        taskDto = TaskDto.builder()
                .id(1L)
                .author(UserDto.builder()
                        .id(1L)
                        .email("admin@example.com")
                        .build())
                .assignee(UserDto.builder()
                        .id(1L)
                        .email("user@example.com")
                        .build())
                .build();

        taskRqDto = TaskRqDto.builder()
                .assigneeId(2L)
                .build();

        comment = Comment.builder()
                .content("Test content")
                .build();

        commentRqDto = CommentRqDto.builder()
                .content("Test content")
                .build();
    }

    @Test
    void createTask_shouldCreateAndReturnTaskDto() {
        when(userService.getUserById(taskRqDto.getAssigneeId())).thenReturn(eligibleUser);
        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(taskMapper.taskRqDtoToTask(taskRqDto)).thenReturn(task);
        when(taskRepository.saveAndFlush(task)).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.createTask(taskRqDto);

        assertThat(result).isEqualTo(taskDto);
    }

    @Test
    void getTasks_shouldReturnTaskList() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Task> tasksPage = new PageImpl<>(List.of(task));
        when(taskRepository.findByAuthorIdAndAssigneeId(1L, 2L, pageRequest)).thenReturn(tasksPage);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        List<TaskDto> result = taskService.getTasks(1L, 2L, 0, 10);

        assertThat(result).hasSize(1).contains(taskDto);
    }

    @Test
    void getTask_shouldReturnTaskDto_whenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.getTask(1L);

        assertThat(result).isEqualTo(taskDto);
    }

    @Test
    void getTask_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verifyNoInteractions(taskMapper);
    }

    @Test
    void updateTask_shouldUpdateTask_whenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskMapper).updateTaskFromTaskRqDto(taskRqDto, task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(1L, taskRqDto);

        assertThat(result).isEqualTo(taskDto);
    }

    @Test
    void updateTask_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(1L, taskRqDto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verifyNoInteractions(taskMapper);
        verify(taskRepository, times(0)).save(any());
    }

    @Test
    void deleteTask_shouldCallRepositoryDelete_whenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verify(taskRepository, times(0)).delete(task);
    }

    @Test
    void updateStatus_shouldUpdateTaskStatus_whenTaskExistsAndPermitted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(permissionService.isPermitted(task)).thenReturn(true);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskStatusDto statusDto = TaskStatusDto.PROCESSING;
        TaskDto result = taskService.updateStatus(1L, statusDto);

        assertThat(result).isEqualTo(taskDto);
        assertThat(task.getStatus()).isEqualTo(Task.Status.PROCESSING);
    }

    @Test
    void updateStatus_shouldThrowAccessDeniedException_whenTaskExistsAndIsNotPermitted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(permissionService.isPermitted(task)).thenReturn(false);

        TaskStatusDto statusDto = TaskStatusDto.PROCESSING;
        assertThatThrownBy(() -> taskService.updateStatus(1L, statusDto))
                .isInstanceOf(AccessDeniedException.class);

        verify(taskRepository, times(0)).save(any());
        verifyNoInteractions(taskMapper);
    }

    @Test
    void updateStatus_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskStatusDto statusDto = TaskStatusDto.PROCESSING;

        assertThatThrownBy(() -> taskService.updateStatus(1L, statusDto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verify(taskRepository, times(0)).save(any());
        verifyNoInteractions(taskMapper);
    }

    @Test
    void updatePriority_shouldUpdatePriority_whenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskPriorityDto priorityDto = TaskPriorityDto.HIGH;
        TaskDto result = taskService.updatePriority(1L, priorityDto);

        assertThat(result).isEqualTo(taskDto);
        assertThat(task.getPriority()).isEqualTo(Task.Priority.HIGH);

        verify(taskRepository).save(task);
    }

    @Test
    void updatePriority_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskPriorityDto priorityDto = TaskPriorityDto.HIGH;
        assertThatThrownBy(() -> taskService.updatePriority(1L, priorityDto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verify(taskRepository, times(0)).save(any());
        verifyNoInteractions(taskMapper);
    }

    @Test
    void submitComment_shouldAddComment_whenPermitted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(permissionService.isPermitted(task)).thenReturn(true);
        when(commentService.submitComment(task, commentRqDto)).thenReturn(comment);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.submitComment(1L, commentRqDto);

        assertThat(result).isEqualTo(taskDto);
        assertThat(task.getComments()).hasSize(1);
        assertThat(task.getComments().get(0)).isEqualTo(comment);
    }

    @Test
    void submitComment_shouldThrowAccessDeniedException_whenIsNotPermitted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(permissionService.isPermitted(task)).thenReturn(false);

        assertThatThrownBy(() -> taskService.submitComment(1L, commentRqDto))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(taskMapper, commentService);
    }

    @Test
    void submitComment_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.submitComment(1L, commentRqDto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verifyNoInteractions(taskMapper, commentService);
    }

    @Test
    void updateAssignee_shouldUpdateAssigneeSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(3L)).thenReturn(ineligibleUser);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateAssignee(1L, 3L);

        assertThat(result).isEqualTo(taskDto);
        assertThat(task.getAssignee()).isEqualTo(ineligibleUser);
    }

    @Test
    void updateAssignee_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateAssignee(1L, 2L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.valueOf(1L));

        verify(taskRepository, times(0)).save(any());
        verifyNoInteractions(taskMapper);
    }
}