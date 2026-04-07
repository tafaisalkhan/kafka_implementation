package com.example.kafkaexmaple.controller;

import com.example.kafkaexmaple.dto.MessageRequest;
import com.example.kafkaexmaple.dto.MessageResponse;
import com.example.kafkaexmaple.service.KafkaProducerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/producer/messages")
public class ProducerController {

    private final KafkaProducerService producerService;

    public ProducerController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse send(@Valid @RequestBody MessageRequest request) {
        producerService.send(request.topic(), request.message());
        return new MessageResponse("accepted", request.topic(), request.message());
    }
}
