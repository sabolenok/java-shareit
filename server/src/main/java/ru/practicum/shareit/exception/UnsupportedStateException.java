package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ValidationException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedStateException extends ValidationException {
    public UnsupportedStateException(String message) {
        super(message);
    }
}
