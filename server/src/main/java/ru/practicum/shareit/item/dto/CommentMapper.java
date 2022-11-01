package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = new User();
        if (authorId != null)
            author.setId(authorId);
        Item item = new Item();
        if (itemId != null)
            item.setId(itemId);
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }
}
