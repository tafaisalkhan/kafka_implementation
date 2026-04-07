package com.example.kafkaexmaple.workflow.listener;

import com.example.kafkaexmaple.workflow.domain.WorkflowStage;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import com.example.kafkaexmaple.workflow.service.WorkflowEventCodec;
import com.example.kafkaexmaple.workflow.service.WorkflowEventPublisher;
import com.example.kafkaexmaple.workflow.store.WorkflowEventStore;
import com.example.kafkaexmaple.workflow.store.WorkflowReservationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QuotaReservationListener {

    private static final Logger log = LoggerFactory.getLogger(QuotaReservationListener.class);

    private final WorkflowEventCodec codec;
    private final WorkflowEventPublisher publisher;
    private final WorkflowEventStore eventStore;
    private final WorkflowReservationStore reservationStore;
    private final String initiatedTopic;
    private final String reservedTopic;
    private final String rejectedTopic;

    public QuotaReservationListener(WorkflowEventCodec codec,
                                    WorkflowEventPublisher publisher,
                                    WorkflowEventStore eventStore,
                                    WorkflowReservationStore reservationStore,
                                    @Value("${app.workflow.topics.initiated}") String initiatedTopic,
                                    @Value("${app.workflow.topics.quota-reserved}") String reservedTopic,
                                    @Value("${app.workflow.topics.quota-rejected}") String rejectedTopic) {
        this.codec = codec;
        this.publisher = publisher;
        this.eventStore = eventStore;
        this.reservationStore = reservationStore;
        this.initiatedTopic = initiatedTopic;
        this.reservedTopic = reservedTopic;
        this.rejectedTopic = rejectedTopic;
    }

    @KafkaListener(topics = "${app.workflow.topics.initiated}", groupId = "${app.workflow.groups.quota}")
    public void onInitiated(String payload) {
        WorkflowEvent event = codec.fromJson(payload);
        eventStore.add("quota-service", initiatedTopic, event);

        if (event.simulateQuotaFailure()) {
            WorkflowEvent rejected = new WorkflowEvent(
                    event.workflowId(),
                    WorkflowStage.QUOTA_REJECTED,
                    "quota-service",
                    event.operation(),
                    event.simulateProvisionFailure(),
                    true,
                    "quota check failed"
            );
            log.info("quota-service rejected workflowId={} resourceType={}", event.workflowId(), event.resourceType());
            publisher.publish(rejectedTopic, rejected);
            return;
        }

        reservationStore.reserve(event.workflowId());
        WorkflowEvent reserved = new WorkflowEvent(
                event.workflowId(),
                WorkflowStage.QUOTA_RESERVED,
                "quota-service",
                event.operation(),
                event.simulateProvisionFailure(),
                false,
                "quota reserved"
        );
        log.info("quota-service reserved workflowId={} resourceType={}", event.workflowId(), event.resourceType());
        publisher.publish(reservedTopic, reserved);
    }
}
