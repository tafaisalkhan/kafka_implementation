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
public class UsageRecordListener {

    private static final Logger log = LoggerFactory.getLogger(UsageRecordListener.class);

    private final WorkflowEventCodec codec;
    private final WorkflowEventPublisher publisher;
    private final WorkflowEventStore eventStore;
    private final String succeededTopic;
    private final String usageTopic;

    public UsageRecordListener(WorkflowEventCodec codec,
                               WorkflowEventPublisher publisher,
                               WorkflowEventStore eventStore,
                               @Value("${app.workflow.topics.provision-succeeded}") String succeededTopic,
                               @Value("${app.workflow.topics.usage-recorded}") String usageTopic) {
        this.codec = codec;
        this.publisher = publisher;
        this.eventStore = eventStore;
        this.succeededTopic = succeededTopic;
        this.usageTopic = usageTopic;
    }

    @KafkaListener(topics = "${app.workflow.topics.provision-succeeded}", groupId = "${app.workflow.groups.usage}")
    public void onProvisionSucceeded(String payload) {
        WorkflowEvent event = codec.fromJson(payload);
        WorkflowEvent usageRecorded = new WorkflowEvent(
                event.workflowId(),
                WorkflowStage.USAGE_RECORDED,
                "usage-service",
                event.operation(),
                event.simulateProvisionFailure(),
                event.simulateQuotaFailure(),
                "usage recorded"
        );
        eventStore.add("usage-service", succeededTopic, event);
        log.info("usage-service recorded usage workflowId={} resourceType={}", event.workflowId(), event.resourceType());
        publisher.publish(usageTopic, usageRecorded);
    }
}
