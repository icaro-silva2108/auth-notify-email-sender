package com.icaro.email_sender.model;

public record UserCreatedEventDTO(

        String name,
        String userEmail
) {}