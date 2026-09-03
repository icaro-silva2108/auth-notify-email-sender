package com.icaro.email_sender.service;

import com.icaro.email_sender.model.UserEventDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendWelcomeEmail(UserEventDTO event) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        final Context context = new Context();
        context.setVariable("username", event.name());

        final String htmlContent = templateEngine.process("welcomeMessage", context);

        mimeHelper.setTo(event.userEmail());
        mimeHelper.setSubject("Welcome to Auth Notify!");
        mimeHelper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    public void sendUserUpdatedMessage(UserEventDTO event) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        final Context context = new Context();
        context.setVariable("username", event.name());

        final String htmlContent = templateEngine.process("userUpdatedMessage", context);

        mimeHelper.setTo(event.userEmail());
        mimeHelper.setSubject("Profile Updated");
        mimeHelper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }
}