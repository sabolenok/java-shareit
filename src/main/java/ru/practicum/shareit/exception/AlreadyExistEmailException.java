package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ValidationException;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistEmailException  extends ValidationException {
    public AlreadyExistEmailException(String message) {
        super(message);
    }
}
