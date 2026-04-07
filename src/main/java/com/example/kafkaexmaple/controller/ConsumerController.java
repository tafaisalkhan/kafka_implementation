package com.example.kafkaexmaple.controller;

import com.example.kafkaexmaple.dto.ConsumedMessage;
import com.example.kafkaexmaple.service.KafkaMessageStore;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumer/messages")
public class ConsumerController {

    private final KafkaMessageStore messageStore;

    public ConsumerController(KafkaMessageStore messageStore) {
        this.messageStore = messageStore;
    }

    @GetMapping
    public List<ConsumedMessage> consumed(@RequestParam(required = false) String topic) {
        if (topic == null || topic.isBlank()) {
            return messageStore.getAll();
        }
        return messageStore.getByTopic(topic);
    }
}
