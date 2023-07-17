package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment mapToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    @Override
    public CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    @Override
    public List<CommentDto> mapToDto(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(mapToDto(comment));
        }
        return result;
    }

}
