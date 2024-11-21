package ru.mirea.lilkhalil.tasks.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.mirea.lilkhalil.tasks.domain.Comment;
import ru.mirea.lilkhalil.tasks.dto.CommentDto;
import ru.mirea.lilkhalil.tasks.dto.CommentRqDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface CommentMapper {
    Comment commentRqDtoToComment(CommentRqDto commentRqDto);
    CommentDto commentToCommentDto(Comment comment);
}
