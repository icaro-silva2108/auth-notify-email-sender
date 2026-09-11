package com.icaro.email_sender.model;

import com.icaro.email_sender.model.enums.UserRole;

public record UserRoleChangedEventDTO(

        String name,
        String userEmail,
        UserRole role
) {}