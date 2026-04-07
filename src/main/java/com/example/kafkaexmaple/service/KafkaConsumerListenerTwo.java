package com.example.kafkaexmaple.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListenerTwo {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerListenerTwo.class);

    private final KafkaMessageStore messageStore;

    public KafkaConsumerListenerTwo(KafkaMessageStore messageStore) {
        this.messageStore = messageStore;
    }

    @KafkaListener(topics = "${app.kafka.consumer-2-topic}", groupId = "${app.kafka.consumer-2-group}")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("consumer-2 received message topic={} value={}", record.topic(), record.value());
        messageStore.add("consumer-2", record.topic(), record.value());
    }
}
