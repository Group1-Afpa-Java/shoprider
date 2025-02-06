package com.group1.shoprider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUsernameOrPasswordException extends RuntimeException{
    public InvalidUsernameOrPasswordException(String message) {
        super(message);
    }
}
