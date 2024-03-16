package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserTooYoungException extends Exception {

    public UserTooYoungException() {
        super("Użytkownik musi mieć co najmniej 16 lat");
    }

}
