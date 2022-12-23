package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemInItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @Transient
    private User requestor;
    @Column(name = "requestor_id", nullable = false)
    private int requestorId;
    private LocalDateTime created;
    @Transient
    private List<ItemInItemRequest> items;
}
