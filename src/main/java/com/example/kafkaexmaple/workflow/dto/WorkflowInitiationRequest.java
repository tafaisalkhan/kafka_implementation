package com.example.kafkaexmaple.workflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record WorkflowInitiationRequest(
        @Valid
        @NotNull(message = "operation must not be null")
        WorkflowOperationRequest operation,

        boolean simulateProvisionFailure,

        boolean simulateQuotaFailure
) {
}
