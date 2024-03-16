package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidTokenException extends Exception {

    public InvalidTokenException () {
        super("Token nie istnieje");
    }

}
