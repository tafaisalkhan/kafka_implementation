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
public class QuotaRevertListener {

    private static final Logger log = LoggerFactory.getLogger(QuotaRevertListener.class);

    private final WorkflowEventCodec codec;
    private final WorkflowEventPublisher publisher;
    private final WorkflowEventStore eventStore;
    private final WorkflowReservationStore reservationStore;
    private final String failedTopic;
    private final String rejectedTopic;
    private final String revertedTopic;

    public QuotaRevertListener(WorkflowEventCodec codec,
                               WorkflowEventPublisher publisher,
                               WorkflowEventStore eventStore,
                               WorkflowReservationStore reservationStore,
                               @Value("${app.workflow.topics.provision-failed}") String failedTopic,
                               @Value("${app.workflow.topics.quota-rejected}") String rejectedTopic,
                               @Value("${app.workflow.topics.quota-reverted}") String revertedTopic) {
        this.codec = codec;
        this.publisher = publisher;
        this.eventStore = eventStore;
        this.reservationStore = reservationStore;
        this.failedTopic = failedTopic;
        this.rejectedTopic = rejectedTopic;
        this.revertedTopic = revertedTopic;
    }

    @KafkaListener(topics = {
            "${app.workflow.topics.provision-failed}",
            "${app.workflow.topics.quota-rejected}"
    }, groupId = "${app.workflow.groups.quota-revert}")
    public void onFailure(String payload) {
        WorkflowEvent event = codec.fromJson(payload);
        reservationStore.release(event.workflowId());
        WorkflowEvent reverted = new WorkflowEvent(
                event.workflowId(),
                WorkflowStage.QUOTA_REVERTED,
                "quota-revert-service",
                event.operation(),
                event.simulateProvisionFailure(),
                event.simulateQuotaFailure(),
                "quota reverted"
        );
        String sourceTopic = event.stage() == WorkflowStage.PROVISION_FAILED ? failedTopic : rejectedTopic;
        eventStore.add("quota-revert-service", sourceTopic, event);
        log.info("quota-revert-service reverted workflowId={} resourceType={}", event.workflowId(), event.resourceType());
        publisher.publish(revertedTopic, reverted);
    }
}
