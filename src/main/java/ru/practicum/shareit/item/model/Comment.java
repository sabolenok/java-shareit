package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    @Transient
    private Item item;
    private int itemId;
    private int authorId;
    @Transient
    private String authorName;
    private LocalDateTime created;

}
