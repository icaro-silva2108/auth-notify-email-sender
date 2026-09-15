package com.icaro.email_sender.emailConsumer;

import com.icaro.email_sender.model.UserEventMessageDTO;
import com.icaro.email_sender.model.UserEventDTO;
import com.icaro.email_sender.model.UserRoleChangedEventDTO;
import com.icaro.email_sender.service.EmailService;

import tools.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailQueueListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "email-queue")
    public void listener(@Payload UserEventMessageDTO message) throws MessagingException {

        switch(message.type()) {
            case USER_CREATED ->  {
                UserEventDTO payload = objectMapper.convertValue(message.payload(), UserEventDTO.class);
                emailService.sendWelcomeEmail(payload);
            }
            case USER_UPDATED ->  {
                UserEventDTO payload = objectMapper.convertValue(message.payload(), UserEventDTO.class);
                emailService.sendUserUpdatedMessage(payload);
            }
            case USER_ROLE_CHANGED -> {
                UserRoleChangedEventDTO payload = objectMapper.convertValue(message.payload(), UserRoleChangedEventDTO.class);
                emailService.sendUserRoleChangedMessage(payload);
            }
            case USER_DEACTIVATED -> {
                UserEventDTO payload = objectMapper.convertValue(message.payload(), UserEventDTO.class);
                emailService.sendUserDeactivatedMessage(payload);
            }
        }
    }
}