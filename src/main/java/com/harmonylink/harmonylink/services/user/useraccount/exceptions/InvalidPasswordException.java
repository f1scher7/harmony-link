package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {
        super("Hasło musi mieć co najmniej 8 znaków, w tym jedną małą literę, jedną dużą literę i jedną cyfrę");
    }

}
