package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    @Autowired
    private final ModelMapper modelMapper;

    public CommentDto toCommentDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    public Comment toComment(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

}
