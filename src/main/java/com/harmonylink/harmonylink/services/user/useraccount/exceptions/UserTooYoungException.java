package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserTooYoungException extends RuntimeException {

    public UserTooYoungException() {
        super("The user's account must be at least 16 years old.");
    }

}
