package com.example.kafkaexmaple.workflow.service;

import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WorkflowInitiationDispatchService {

    private final WorkflowEventPublisher publisher;

    public WorkflowInitiationDispatchService(WorkflowEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Async
    public void publishAsync(String topic, WorkflowEvent event) {
        publisher.publish(topic, event);
    }
}
