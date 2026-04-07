package com.example.kafkaexmaple.dto;

public record ConsumedMessage(String consumer, String topic, String message) {
}
