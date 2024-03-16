package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class InvalidNicknameException extends Exception {

    public InvalidNicknameException() {
        super("Nickname ma zawierać min. 1 symbol");
    }

}
