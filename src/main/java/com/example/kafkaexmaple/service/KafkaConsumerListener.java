package com.example.kafkaexmaple.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerListener.class);

    private final KafkaMessageStore messageStore;

    public KafkaConsumerListener(KafkaMessageStore messageStore) {
        this.messageStore = messageStore;
    }

    @KafkaListener(topics = "${app.kafka.consumer-1-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("consumer-1 received message topic={} value={}", record.topic(), record.value());
        messageStore.add("consumer-1", record.topic(), record.value());
    }
}
