package com.example.kafkaexmaple.workflow.service;

import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import com.example.kafkaexmaple.service.KafkaPublishService;
import org.springframework.stereotype.Service;

@Service
public class WorkflowEventPublisher {

    private final KafkaPublishService publishService;
    private final WorkflowEventCodec codec;

    public WorkflowEventPublisher(KafkaPublishService publishService, WorkflowEventCodec codec) {
        this.publishService = publishService;
        this.codec = codec;
    }

    public void publish(String topic, WorkflowEvent event) {
        try {

            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        publishService.send(topic, event.workflowId(), codec.toJson(event));
    }
}
