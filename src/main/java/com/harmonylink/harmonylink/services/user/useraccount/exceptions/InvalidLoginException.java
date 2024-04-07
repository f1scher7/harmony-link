package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidLoginException extends Exception {

    public InvalidLoginException() {
        super("Login musi mieć od 4 do 12 znaków i nie może zawierać symboli specjalnych");
    }

}
