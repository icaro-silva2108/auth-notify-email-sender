package com.icaro.email_sender.model;

import com.icaro.email_sender.model.enums.UserEventType;
import tools.jackson.databind.JsonNode;

public record UserEventMessageDTO(

    UserEventType type,
    JsonNode payload
) {}