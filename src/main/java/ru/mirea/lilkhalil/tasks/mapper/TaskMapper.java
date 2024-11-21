package ru.mirea.lilkhalil.tasks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.dto.TaskDto;
import ru.mirea.lilkhalil.tasks.dto.TaskRqDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, CommentMapper.class})
public interface TaskMapper {
    Task taskRqDtoToTask(TaskRqDto taskDto);
    TaskDto taskToTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    void updateTaskFromTaskRqDto(TaskRqDto dto, @MappingTarget Task task);
}
