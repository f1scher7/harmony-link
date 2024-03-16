package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class EmailNotFoundException extends Exception {

    public EmailNotFoundException() {
        super("Nie możemy wysłać wiadmości z potwierdzeniem na ten mail");
    }

}
