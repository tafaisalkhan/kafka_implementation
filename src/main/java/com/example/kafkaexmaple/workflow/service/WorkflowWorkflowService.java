package com.example.kafkaexmaple.workflow.service;

import com.example.kafkaexmaple.workflow.domain.WorkflowStage;
import com.example.kafkaexmaple.workflow.dto.WorkflowIdResponse;
import com.example.kafkaexmaple.workflow.dto.WorkflowInitiationRequest;
import com.example.kafkaexmaple.workflow.dto.WorkflowOperationDetails;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import com.example.kafkaexmaple.workflow.store.WorkflowEventStore;
import com.example.kafkaexmaple.workflow.util.WorkflowOperationResolver;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WorkflowWorkflowService {

    private final WorkflowInitiationDispatchService initiationDispatchService;
    private final WorkflowEventStore eventStore;
    private final WorkflowOperationResolver operationResolver;
    private final String initiatedTopic;

    public WorkflowWorkflowService(WorkflowInitiationDispatchService initiationDispatchService,
                                   WorkflowEventStore eventStore,
                                   WorkflowOperationResolver operationResolver,
                                   @Value("${app.workflow.topics.initiated}") String initiatedTopic) {
        this.initiationDispatchService = initiationDispatchService;
        this.eventStore = eventStore;
        this.operationResolver = operationResolver;
        this.initiatedTopic = initiatedTopic;
    }

    public WorkflowIdResponse initiate(WorkflowInitiationRequest request) {
        String workflowId = UUID.randomUUID().toString();
        WorkflowOperationDetails operation = operationResolver.resolve(request.operation());
        WorkflowEvent event = new WorkflowEvent(
                workflowId,
                WorkflowStage.INITIATED,
                "api",
                operation,
                request.simulateProvisionFailure(),
                request.simulateQuotaFailure(),
                "workflow initiated"
        );
        eventStore.add("api", initiatedTopic, event);
        initiationDispatchService.publishAsync(initiatedTopic, event);
        return new WorkflowIdResponse(workflowId);
    }
}
