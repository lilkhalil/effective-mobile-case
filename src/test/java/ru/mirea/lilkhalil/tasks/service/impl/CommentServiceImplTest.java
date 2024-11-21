package ru.mirea.lilkhalil.tasks.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mirea.lilkhalil.tasks.domain.Comment;
import ru.mirea.lilkhalil.tasks.domain.Task;
import ru.mirea.lilkhalil.tasks.domain.User;
import ru.mirea.lilkhalil.tasks.dto.CommentRqDto;
import ru.mirea.lilkhalil.tasks.mapper.CommentMapper;
import ru.mirea.lilkhalil.tasks.repository.CommentRepository;
import ru.mirea.lilkhalil.tasks.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Task task;
    private CommentRqDto commentRqDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .build();

        task = Task.builder()
                .id(1L)
                .title("Sample Task")
                .build();

        commentRqDto = CommentRqDto.builder()
                .content("This is a test comment.")
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("This is a test comment.")
                .createdAt(LocalDateTime.now())
                .author(user)
                .build();
    }

    @Test
    void submitComment_shouldSaveAndReturnComment() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(commentMapper.commentRqDtoToComment(commentRqDto)).thenReturn(comment);
        when(commentRepository.saveAndFlush(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.submitComment(task, commentRqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(commentRqDto.getContent());
        assertThat(result.getAuthor()).isEqualTo(user);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getTask()).isNotNull();
    }
}