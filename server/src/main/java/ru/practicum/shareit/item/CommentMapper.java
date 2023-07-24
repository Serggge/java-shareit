package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import java.util.List;

public interface CommentMapper {

    Comment mapToComment(CommentDto commentDto);

    CommentDto mapToDto(Comment comment);

    List<CommentDto> mapToDto(Iterable<Comment> comments);

}
