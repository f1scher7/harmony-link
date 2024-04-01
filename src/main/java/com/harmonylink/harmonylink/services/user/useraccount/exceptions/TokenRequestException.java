package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class TokenRequestException extends Exception{

    public TokenRequestException(String message) {
        super(message);
    }

    public TokenRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
