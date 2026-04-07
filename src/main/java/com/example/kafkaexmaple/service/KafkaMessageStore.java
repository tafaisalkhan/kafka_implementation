package com.example.kafkaexmaple.service;

import com.example.kafkaexmaple.dto.ConsumedMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageStore {

    private final ConcurrentLinkedQueue<ConsumedMessage> messages = new ConcurrentLinkedQueue<>();

    public void add(String consumer, String topic, String message) {
        messages.add(new ConsumedMessage(consumer, topic, message));
    }

    public List<ConsumedMessage> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public List<ConsumedMessage> getByTopic(String topic) {
        return messages.stream()
                .filter(message -> message.topic().equals(topic))
                .toList();
    }
}
