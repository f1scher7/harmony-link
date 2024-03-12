package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException() {
        super("The login credentials are invalid.");
    }

}
