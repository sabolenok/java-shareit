package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    @NotBlank(message = "Наименование не может быть пустым")
    private String name;
    @Column(nullable = false)
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @Column(nullable = false)
    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Column(name = "request_id", nullable = false)
    private int requestId;
    @Transient
    private User owner;
    @Transient
    private ItemRequest request;
}
