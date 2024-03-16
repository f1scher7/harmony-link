package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class WrongPasswordException extends Exception {

    public WrongPasswordException() {
        super("Nieprawidłowe hasło");
    }

}
