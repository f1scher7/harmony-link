package com.harmonylink.harmonylink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.mail")
public class EmailConfig {

    private String verificationSubject;
    private String resetPasswordSubject;
    private String changeEmailSubject;


    public String getVerificationSubject() {
        return this.verificationSubject;
    }

    public String getResetPasswordSubject() {
        return this.resetPasswordSubject;
    }

    public String getChangeEmailSubject() {
        return this.changeEmailSubject;
    }


    public void setVerificationSubject(String verificationSubject) {
        this.verificationSubject = verificationSubject;
    }

    public void setResetPasswordSubject(String resetPasswordSubject) {
        this.resetPasswordSubject = resetPasswordSubject;
    }

    public void setChangeEmailSubject(String changeEmailSubject) {
        this.changeEmailSubject = changeEmailSubject;
    }

}
