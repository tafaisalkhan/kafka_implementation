package com.example.kafkaexmaple.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank(message = "topic must not be blank")
        String topic,
        @NotBlank(message = "message must not be blank")
        String message
) {
}
