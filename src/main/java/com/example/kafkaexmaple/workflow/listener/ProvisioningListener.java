package com.example.kafkaexmaple.workflow.listener;

import com.example.kafkaexmaple.workflow.domain.WorkflowStage;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import com.example.kafkaexmaple.workflow.service.WorkflowEventCodec;
import com.example.kafkaexmaple.workflow.service.WorkflowEventPublisher;
import com.example.kafkaexmaple.workflow.store.WorkflowEventStore;
import com.example.kafkaexmaple.workflow.store.WorkflowReservationStore;
import com.example.kafkaexmaple.workflow.provider.ProvisioningAdapterRegistry;
import com.example.kafkaexmaple.workflow.provider.ProvisioningOutcome;
import com.example.kafkaexmaple.workflow.provider.ResourceProvisioningAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProvisioningListener {

    private static final Logger log = LoggerFactory.getLogger(ProvisioningListener.class);

    private final WorkflowEventCodec codec;
    private final WorkflowEventPublisher publisher;
    private final WorkflowEventStore eventStore;
    private final WorkflowReservationStore reservationStore;
    private final ProvisioningAdapterRegistry adapterRegistry;
    private final String reservedTopic;
    private final String succeededTopic;
    private final String failedTopic;

    public ProvisioningListener(WorkflowEventCodec codec,
                                WorkflowEventPublisher publisher,
                                WorkflowEventStore eventStore,
                                WorkflowReservationStore reservationStore,
                                ProvisioningAdapterRegistry adapterRegistry,
                                @Value("${app.workflow.topics.quota-reserved}") String reservedTopic,
                                @Value("${app.workflow.topics.provision-succeeded}") String succeededTopic,
                                @Value("${app.workflow.topics.provision-failed}") String failedTopic) {
        this.codec = codec;
        this.publisher = publisher;
        this.eventStore = eventStore;
        this.reservationStore = reservationStore;
        this.adapterRegistry = adapterRegistry;
        this.reservedTopic = reservedTopic;
        this.succeededTopic = succeededTopic;
        this.failedTopic = failedTopic;
    }

    @KafkaListener(topics = "${app.workflow.topics.quota-reserved}", groupId = "${app.workflow.groups.provision}")
    public void onQuotaReserved(String payload) {
        WorkflowEvent event = codec.fromJson(payload);
        eventStore.add("provision-service", reservedTopic, event);

        if (!reservationStore.isReserved(event.workflowId())) {
            log.warn("provision-service skipped workflowId={} because quota is not reserved", event.workflowId());
            return;
        }

        ResourceProvisioningAdapter adapter = adapterRegistry.resolve(event.provider());
        ProvisioningOutcome outcome = adapter.provision(event);

        if (!outcome.success()) {
            WorkflowEvent failed = new WorkflowEvent(
                    event.workflowId(),
                    WorkflowStage.PROVISION_FAILED,
                    "provision-service",
                    event.operation(),
                    event.simulateProvisionFailure(),
                    event.simulateQuotaFailure(),
                    outcome.details()
            );
            log.info("provision-service failed workflowId={} provider={} resourceType={}",
                    event.workflowId(), event.provider(), event.resourceType());
            publisher.publish(failedTopic, failed);
            return;
        }

        WorkflowEvent succeeded = new WorkflowEvent(
                event.workflowId(),
                WorkflowStage.PROVISION_SUCCEEDED,
                "provision-service",
                event.operation(),
                event.simulateProvisionFailure(),
                event.simulateQuotaFailure(),
                outcome.details()
        );
        log.info("provision-service succeeded workflowId={} provider={} resourceType={}",
                event.workflowId(), event.provider(), event.resourceType());
        publisher.publish(succeededTopic, succeeded);
    }
}
