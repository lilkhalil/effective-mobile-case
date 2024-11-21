package ru.mirea.lilkhalil.tasks.service;

import org.springframework.security.access.AccessDeniedException;
import ru.mirea.lilkhalil.tasks.dto.*;
import ru.mirea.lilkhalil.tasks.exception.TaskNotFoundException;

import java.util.List;

/**
 * Сервис для управления задачами.
 * Предоставляет методы для создания, получения, обновления, удаления и изменения различных атрибутов задач.
 */
public interface TaskService {

    /**
     * Создаёт новую задачу на основе переданных данных.
     *
     * @param taskRqDto объект {@link TaskRqDto}, содержащий информацию о создаваемой задаче.
     * @return объект {@link TaskDto}, представляющий созданную задачу.
     */
    TaskDto createTask(TaskRqDto taskRqDto);

    /**
     * Возвращает список задач с возможностью фильтрации по автору и исполнителю, а также возможностью пагинации.
     *
     * @param authorId   идентификатор автора задач (опционально).
     * @param assigneeId идентификатор исполнителя задач (опционально).
     * @param pageNumber номер страницы для пагинации.
     * @param pageSize   количество задач на странице для пагинации.
     * @return список объектов {@link TaskDto}, представляющих задачи.
     */
    List<TaskDto> getTasks(Long authorId, Long assigneeId, Integer pageNumber, Integer pageSize);

    /**
     * Возвращает задачу по её идентификатору.
     *
     * @param taskId идентификатор задачи.
     * @return объект {@link TaskDto}, представляющий найденную задачу.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */
    TaskDto getTask(Long taskId) throws TaskNotFoundException;

    /**
     * Обновляет данные существующей задачи.
     *
     * @param taskId    идентификатор задачи.
     * @param taskRqDto объект {@link TaskRqDto}, содержащий обновлённые данные задачи.
     * @return объект {@link TaskDto}, представляющий обновлённую задачу.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */
    TaskDto updateTask(Long taskId, TaskRqDto taskRqDto) throws TaskNotFoundException;

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param taskId идентификатор задачи.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */
    void deleteTask(Long taskId) throws TaskNotFoundException;

    /**
     * Обновляет статус задачи.
     *
     * @param taskId идентификатор задачи.
     * @param status объект {@link TaskStatusDto}, содержащий новый статус задачи.
     * @return объект {@link TaskDto}, представляющий задачу с обновлённым статусом.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     * @throws AccessDeniedException если у пользователя нет прав к изменению статуса задачи
     */
    TaskDto updateStatus(Long taskId, TaskStatusDto status) throws TaskNotFoundException, AccessDeniedException;

    /**
     * Обновляет приоритет задачи.
     *
     * @param taskId  идентификатор задачи.
     * @param priority объект {@link TaskPriorityDto}, содержащий новый приоритет задачи.
     * @return объект {@link TaskDto}, представляющий задачу с обновлённым приоритетом.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */
    TaskDto updatePriority(Long taskId, TaskPriorityDto priority) throws TaskNotFoundException;

    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId       идентификатор задачи.
     * @param commentRqDto объект {@link CommentRqDto}, содержащий текст комментария.
     * @return объект {@link TaskDto}, представляющий задачу с добавленным комментарием.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     * @throws AccessDeniedException если у пользователя нет прав к комментированию задачи
     */
    TaskDto submitComment(Long taskId, CommentRqDto commentRqDto) throws TaskNotFoundException, AccessDeniedException;

    /**
     * Обновляет исполнителя задачи.
     *
     * @param taskId    идентификатор задачи.
     * @param assigneeId идентификатор нового исполнителя.
     * @return объект {@link TaskDto}, представляющий задачу с обновлённым исполнителем.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */
    TaskDto updateAssignee(Long taskId, Long assigneeId) throws TaskNotFoundException;
}
