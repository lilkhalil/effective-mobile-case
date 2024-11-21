package ru.mirea.lilkhalil.tasks.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.mirea.lilkhalil.tasks.api.TasksApiDelegate;
import ru.mirea.lilkhalil.tasks.dto.*;
import ru.mirea.lilkhalil.tasks.service.TaskService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TasksApiDelegateImpl implements TasksApiDelegate {

    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskDto> createTask(TaskRqDto taskRqDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskRqDto));
    }

    @Override
    public ResponseEntity<List<TaskDto>> getTasks(Integer page, Integer size, Long author, Long assignee) {
        return ResponseEntity.ok(taskService.getTasks(author, assignee, page, size));
    }

    @Override
    public ResponseEntity<TaskDto> getTask(Long taskId) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }

    @Override
    public ResponseEntity<TaskDto> updateTask(Long taskId, TaskRqDto taskRqDto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskRqDto));
    }

    @Override
    public ResponseEntity<Void> deleteTask(Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent()
                .build();
    }

    @Override
    public ResponseEntity<TaskDto> updateStatus(Long taskId, TaskStatusDto status) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status));
    }

    @Override
    public ResponseEntity<TaskDto> updatePriority(Long taskId, TaskPriorityDto priority) {
        return ResponseEntity.ok(taskService.updatePriority(taskId, priority));
    }

    @Override
    public ResponseEntity<TaskDto> submitComment(Long taskId, CommentRqDto commentRqDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.submitComment(taskId, commentRqDto));
    }

    @Override
    public ResponseEntity<TaskDto> updateAssignee(Long taskId, Long assigneeId) {
        return ResponseEntity.ok(taskService.updateAssignee(taskId, assigneeId));
    }
}
