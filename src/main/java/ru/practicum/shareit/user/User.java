package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    @NotBlank(message = "Логин не может быть пустым")
    private String name;
    @Column(nullable = false)
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
}
