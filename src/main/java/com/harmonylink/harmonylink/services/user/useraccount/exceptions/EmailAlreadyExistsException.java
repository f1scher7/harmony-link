package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class EmailAlreadyExistsException extends Exception {

    public EmailAlreadyExistsException(String email) {
        super("Mail " + email + " już istnieje");
    }

}
