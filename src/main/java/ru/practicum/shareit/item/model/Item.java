package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingInItem;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    @Column(name = "is_available", nullable = false)
    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private int userId;
    @Column(name = "request_id")
    private int requestId;
    @Transient
    private User owner;
    @Transient
    private ItemRequest request;
    @Transient
    private BookingInItem lastBooking;
    @Transient
    private BookingInItem nextBooking;
    @Transient
    private List<Comment> comments;
}
