package com.icaro.email_sender.emailConsumer;

import com.icaro.email_sender.model.UserCreatedEventDTO;
import com.icaro.email_sender.service.EmailService;

import jakarta.mail.MessagingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailQueueListener {

    private final EmailService emailService;

    @RabbitListener(queues = "email-queue")
    public void listener(@Payload UserCreatedEventDTO message) throws MessagingException {

        emailService.sendWelcomeEmail(message);
    }
}