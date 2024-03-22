package com.harmonylink.harmonylink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.mail")
public class EmailConfig {

    private String verificationSubject;
    private String resetPasswordSubject;


    public String getVerificationSubject() {
        return verificationSubject;
    }

    public String getResetPasswordSubject() {
        return resetPasswordSubject;
    }


    public void setVerificationSubject(String verificationSubject) {
        this.verificationSubject = verificationSubject;
    }

    public void setResetPasswordSubject(String resetPasswordSubject) {
        this.resetPasswordSubject = resetPasswordSubject;
    }

}
