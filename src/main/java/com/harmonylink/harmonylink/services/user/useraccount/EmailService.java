package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.config.EmailConfig;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.EmailNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailConfig emailConfig;


    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine, EmailConfig emailConfig) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.emailConfig = emailConfig;
    }


    public void sendVerificationEmail(UserAccount userAccount, String token) throws EmailNotFoundException {
        Context context = new Context();
        context.setVariable("login", userAccount.getLogin());
        context.setVariable("verificationLink", "https://192.168.0.102:8443/auth/activate-account?token=" + token);

        sendEmail(userAccount.getEmail(), this.emailConfig.getVerificationSubject(), "emailTemplates/verificationEmailTemplate", context);
    }

    public void sendResetPasswordEmail(UserAccount userAccount, String token) throws EmailNotFoundException {
        Context context = new Context();
        context.setVariable("login", userAccount.getLogin());
        context.setVariable("resetPasswordLink", "https://192.168.0.102:8443/auth/reset-password?token=" + token);

        sendEmail(userAccount.getEmail(), this.emailConfig.getResetPasswordSubject(), "emailTemplates/resetPasswordTemplate", context);
    }

    public void sendChangeEmailEmail(String login, String token, String newEmail) throws EmailNotFoundException {
        Context context = new Context();
        context.setVariable("login", login);
        context.setVariable("changeEmailLink", "https://192.168.0.102:8443/auth/change-email?token=" + token);

        sendEmail(newEmail, this.emailConfig.getChangeEmailSubject(), "emailTemplates/changeEmailTemplate", context);
    }


    @Async
    public void  sendEmail(String userMail, String subject, String templateName, Context context) throws EmailNotFoundException {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            String body = this.templateEngine.process(templateName, context);
            String harmonyEmail = System.getenv("HARMONYLINK_EMAIL");
            helper.setFrom(harmonyEmail);
            helper.setTo(userMail);
            helper.setSubject(subject);
            helper.setText(body, true);

            this.javaMailSender.send(mimeMessage);
        } catch (MailSendException | MessagingException e) {
            throw new EmailNotFoundException(userMail);
        }
    }

}
