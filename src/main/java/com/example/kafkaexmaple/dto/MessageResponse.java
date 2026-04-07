package com.example.kafkaexmaple.dto;

public record MessageResponse(String status, String topic, String message) {
}
