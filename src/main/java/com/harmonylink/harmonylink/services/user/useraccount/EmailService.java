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
            throw new EmailNotFoundException();
        }
    }

    public void sendVerificationEmail(UserAccount userAccount, String token) throws EmailNotFoundException {
        Context context = new Context();
        context.setVariable("login", userAccount.getLogin());
        context.setVariable("verificationLink", "http://3.70.228.63/activate-account?token=" + token);

        sendEmail(userAccount.getEmail(), this.emailConfig.getVerificationSubject(), "emailTemplates/verificationEmailTemplate", context);
    }

    public void sendResetPasswordEmail(UserAccount userAccount, String token) throws EmailNotFoundException {
        Context context = new Context();
        context.setVariable("login", userAccount.getLogin());
        context.setVariable("resetPasswordLink", "http://3.70.228.63/reset-password?token=" + token);

        sendEmail(userAccount.getEmail(), this.emailConfig.getResetPasswordSubject(), "emailTemplates/resetPasswordTemplate", context);
    }

}
