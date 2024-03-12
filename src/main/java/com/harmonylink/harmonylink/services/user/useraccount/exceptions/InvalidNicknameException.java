package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidNicknameException extends Exception {

    public InvalidNicknameException() {
        super("Nickname has to contain at least 1 symbol.");
    }

}
