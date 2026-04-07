package com.example.kafkaexmaple.workflow.dto;

import com.example.kafkaexmaple.workflow.domain.ResourceType;
import com.example.kafkaexmaple.workflow.domain.ProviderType;
import com.example.kafkaexmaple.workflow.domain.WorkflowStage;

public record WorkflowEvent(
        String workflowId,
        WorkflowStage stage,
        String actor,
        WorkflowOperationDetails operation,
        boolean simulateProvisionFailure,
        boolean simulateQuotaFailure,
        String details
) {

    public ProviderType provider() {
        return operation.provider();
    }

    public ResourceType resourceType() {
        return operation.resourceType();
    }

    public String resourceName() {
        return operation.resourceName();
    }

    public String operationName() {
        return operation.name();
    }
}
