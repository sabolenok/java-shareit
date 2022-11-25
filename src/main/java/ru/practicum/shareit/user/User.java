package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {
    private int id;
    @NotBlank(message = "Логин не может быть пустым")
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
}
