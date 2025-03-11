package com.usermanager.manager.infra.mail;

import org.springframework.stereotype.Service;

import com.usermanager.manager.model.email.MailProvider;

@Service
public class MailService {
    private static final String API_PREFIX = "http://localhost:8080/api/auth/register/confirm?token=";
    public final MailProvider mailProvider;

    public MailService(MailProvider mailProvider) {
        this.mailProvider = mailProvider;
    }

    public void sendVerificationMail(String recipient, String token) {
        String subject = "Verify your e-mail";
        String body = "Click here to activate your account: " + API_PREFIX + token;
        mailProvider.sendEmail(recipient, subject, body);
    }
}
