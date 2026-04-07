package com.example.kafkaexmaple.workflow.dto;

import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkflowOperationRequest(
        @NotBlank(message = "operation.name must not be blank")
        String name,

        @NotBlank(message = "operation.type must not be blank")
        String type,

        @NotNull(message = "operation.contractId must not be null")
        Long contractId,

        @NotNull(message = "operation.productId must not be null")
        Long productId,

        @NotNull(message = "operation.providerId must not be null")
        Long providerId,

        @NotNull(message = "operation.parameters must not be null")
        Map<String, Object> parameters,

        @NotBlank(message = "operation.region must not be blank")
        String region,

        @NotBlank(message = "operation.otherParameters must not be blank")
        String otherParameters
) {
}
