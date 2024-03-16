package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException() {
        super("Nieprawidłowe dane do logowania");
    }

}
