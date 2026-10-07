package com.icaro.email_sender.emailConsumer;

import com.icaro.email_sender.model.UserEventMessageDTO;
import com.icaro.email_sender.model.UserEventDTO;
import com.icaro.email_sender.model.UserRoleChangedEventDTO;
import com.icaro.email_sender.service.EmailService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.node.ObjectNode;
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
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "email-queue")
    public void listener(@Payload UserEventMessageDTO message) throws MessagingException {

        ObjectNode payloadNode = (ObjectNode) message.payload();
        int retryCount = message.payload().path("retryCount").asInt(0);

        if(retryCount >= 5) {

            rabbitTemplate.convertAndSend(
                    "dead-email-exchange",
                    "dead.email",
                    message
            );
            return;
        }

        try {
            switch(message.type()) {
                case USER_CREATED -> {
                    UserEventDTO payload = objectMapper.convertValue(message.payload(), UserEventDTO.class);
                    emailService.sendWelcomeEmail(payload);
                }
                case USER_UPDATED -> {
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
        catch(Exception e) {

            payloadNode.put("retryCount", retryCount + 1);
            rabbitTemplate.convertAndSend(
                    "retry-email-exchange",
                    "email.retry",
                    message
            );
        }
    }
}