package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidGenderException extends Exception {

    public InvalidGenderException() {
        super("There are 2 possible genders: Male or Female.");
    }

}
