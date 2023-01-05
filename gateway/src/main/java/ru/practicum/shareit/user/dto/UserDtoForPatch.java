package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoForPatch {
    private String name;
    @Email(message = "Электронная почта не соответствует формату")
    private String email;
}
