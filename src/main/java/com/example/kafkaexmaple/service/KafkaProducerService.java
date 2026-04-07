package com.example.kafkaexmaple.service;

import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaPublishService publishService;

    public KafkaProducerService(KafkaPublishService publishService) {
        this.publishService = publishService;
    }

    public void send(String topic, String message) {
        publishService.send(topic, message);
    }
}
