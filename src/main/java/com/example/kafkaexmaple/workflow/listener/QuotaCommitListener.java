package com.example.kafkaexmaple.workflow.listener;

import com.example.kafkaexmaple.workflow.domain.WorkflowStage;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import com.example.kafkaexmaple.workflow.service.WorkflowEventCodec;
import com.example.kafkaexmaple.workflow.service.WorkflowEventPublisher;
import com.example.kafkaexmaple.workflow.store.WorkflowEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QuotaCommitListener {

    private static final Logger log = LoggerFactory.getLogger(QuotaCommitListener.class);

    private final WorkflowEventCodec codec;
    private final WorkflowEventPublisher publisher;
    private final WorkflowEventStore eventStore;
    private final String succeededTopic;
    private final String committedTopic;

    public QuotaCommitListener(WorkflowEventCodec codec,
                               WorkflowEventPublisher publisher,
                               WorkflowEventStore eventStore,
                               @Value("${app.workflow.topics.provision-succeeded}") String succeededTopic,
                               @Value("${app.workflow.topics.quota-committed}") String committedTopic) {
        this.codec = codec;
        this.publisher = publisher;
        this.eventStore = eventStore;
        this.succeededTopic = succeededTopic;
        this.committedTopic = committedTopic;
    }

    @KafkaListener(topics = "${app.workflow.topics.provision-succeeded}", groupId = "${app.workflow.groups.quota-commit}")
    public void onProvisionSucceeded(String payload) {
        WorkflowEvent event = codec.fromJson(payload);
        WorkflowEvent committed = new WorkflowEvent(
                event.workflowId(),
                WorkflowStage.QUOTA_COMMITTED,
                "quota-commit-service",
                event.operation(),
                event.simulateProvisionFailure(),
                event.simulateQuotaFailure(),
                "quota committed"
        );
        eventStore.add("quota-commit-service", succeededTopic, event);
        log.info("quota-commit-service committed workflowId={} resourceType={}", event.workflowId(), event.resourceType());
        publisher.publish(committedTopic, committed);
    }
}
