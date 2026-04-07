package com.example.kafkaexmaple.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class KafkaPublishService {

    private static final Logger log = LoggerFactory.getLogger(KafkaPublishService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int maxAttempts;
    private final long retryBackoffMs;
    private final long publishDelayMs;

    public KafkaPublishService(KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${app.kafka.publish.max-attempts:5}") int maxAttempts,
                               @Value("${app.kafka.publish.retry-backoff-ms:200}") long retryBackoffMs,
                               @Value("${app.kafka.publish.delay-ms:0}") long publishDelayMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.retryBackoffMs = Math.max(0L, retryBackoffMs);
        this.publishDelayMs = Math.max(0L, publishDelayMs);
    }

    public void send(String topic, String key, String value) {
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                sleepBeforePublish();
                kafkaTemplate.send(topic, key, value).get(10, TimeUnit.SECONDS);
                return;
            } catch (Exception ex) {
                lastFailure = new IllegalStateException(
                        "Failed to publish Kafka message to topic=" + topic + " after attempt=" + attempt,
                        ex
                );
                if (attempt < maxAttempts) {
                    log.warn("Kafka publish failed topic={} attempt={}/{}; retrying", topic, attempt, maxAttempts, ex);
                    sleepQuietly();
                }
            }
        }

        throw lastFailure;
    }

    public void send(String topic, String value) {
        send(topic, null, value);
    }

    private void sleepQuietly() {
        if (retryBackoffMs <= 0) {
            return;
        }
        try {
            Thread.sleep(retryBackoffMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting to retry Kafka publish", ex);
        }
    }

    private void sleepBeforePublish() {
        if (publishDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(publishDelayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while simulating Kafka publish delay", ex);
        }
    }
}
