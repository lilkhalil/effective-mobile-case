package ru.mirea.lilkhalil.tasks.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.mirea.lilkhalil.tasks.domain.Comment;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.dto.*;
import ru.mirea.lilkhalil.tasks.exception.TaskNotFoundException;
import ru.mirea.lilkhalil.tasks.mapper.TaskMapper;
import ru.mirea.lilkhalil.tasks.repository.TaskRepository;
import ru.mirea.lilkhalil.tasks.service.CommentService;
import ru.mirea.lilkhalil.tasks.service.PermissionService;
import ru.mirea.lilkhalil.tasks.service.TaskService;
import ru.mirea.lilkhalil.tasks.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private final UserService userService;
    private final CommentService commentService;
    private final PermissionService<Task> permissionService;

    @Override
    public TaskDto createTask(TaskRqDto taskRqDto) {

        Task task = taskMapper.taskRqDtoToTask(taskRqDto);

        User assignee = userService.getUserById(taskRqDto.getAssigneeId());
        User author = userService.getAuthenticatedUser();

        task.setAuthor(author);
        task.setAssignee(assignee);

        taskRepository.saveAndFlush(task);

        return taskMapper.taskToTaskDto(task);
    }

    @Override
    public List<TaskDto> getTasks(Long authorId, Long assigneeId, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Task> tasks = taskRepository.findByAuthorIdAndAssigneeId(authorId, assigneeId, pageRequest);

        return tasks.stream()
                .map(taskMapper::taskToTaskDto)
                .toList();
    }

    @Override
    public TaskDto getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .map(taskMapper::taskToTaskDto)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Override
    public TaskDto updateTask(Long taskId, TaskRqDto taskRqDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        taskMapper.updateTaskFromTaskRqDto(taskRqDto, task);

        task.setAssignee(userService.getUserById(taskRqDto.getAssigneeId()));

        return taskMapper.taskToTaskDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        taskRepository.delete(task);
    }

    @Override
    public TaskDto updateStatus(Long taskId, TaskStatusDto status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (!permissionService.isPermitted(task)) {
            throw new AccessDeniedException("You do not have permission to change the status of this task");
        }

        task.setStatus(Task.Status.valueOf(status.name()));

        return taskMapper.taskToTaskDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updatePriority(Long taskId, TaskPriorityDto priority) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.setPriority(Task.Priority.valueOf(priority.name()));

        return taskMapper.taskToTaskDto(taskRepository.save(task));
    }

    @Override
    public TaskDto submitComment(Long taskId, CommentRqDto commentRqDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (!permissionService.isPermitted(task)) {
            throw new AccessDeniedException("You do not have permission to submit comment to this task");
        }

        List<Comment> comments = task.getComments();

        Comment comment = commentService.submitComment(task, commentRqDto);

        comments.add(comment);

        return taskMapper.taskToTaskDto(task);
    }

    @Override
    public TaskDto updateAssignee(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.setAssignee(userService.getUserById(assigneeId));

        return taskMapper.taskToTaskDto(taskRepository.save(task));
    }
}
