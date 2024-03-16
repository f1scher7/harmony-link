package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserAlreadyExistsException extends Exception{

    public UserAlreadyExistsException(String login) {
        super("Login \"" + login + "\" jest zajęty");
    }

}
