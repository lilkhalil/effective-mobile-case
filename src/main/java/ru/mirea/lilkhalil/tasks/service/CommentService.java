package ru.mirea.lilkhalil.tasks.service;

import ru.mirea.lilkhalil.tasks.domain.Comment;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.dto.CommentRqDto;

public interface CommentService {
    Comment submitComment(Task task, CommentRqDto commentRqDto);
}
