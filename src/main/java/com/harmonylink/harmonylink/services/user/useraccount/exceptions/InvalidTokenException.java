package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidTokenException extends Exception {

    public InvalidTokenException () {
        super("This token doesn't exist");
    }

}
