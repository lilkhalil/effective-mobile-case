package ru.mirea.lilkhalil.tasks.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.lilkhalil.tasks.domain.Comment;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.dto.CommentRqDto;
import ru.mirea.lilkhalil.tasks.mapper.CommentMapper;
import ru.mirea.lilkhalil.tasks.repository.CommentRepository;
import ru.mirea.lilkhalil.tasks.service.CommentService;
import ru.mirea.lilkhalil.tasks.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public Comment submitComment(Task task, CommentRqDto commentRqDto) {
        Comment comment = commentMapper.commentRqDtoToComment(commentRqDto)
                .setTask(task)
                .setCreatedAt(LocalDateTime.now())
                .setAuthor(userService.getAuthenticatedUser());

        commentRepository.saveAndFlush(comment);

        return comment;
    }
}
