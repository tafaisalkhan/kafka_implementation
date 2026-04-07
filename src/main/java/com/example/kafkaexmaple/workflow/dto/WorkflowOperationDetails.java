package com.example.kafkaexmaple.workflow.dto;

import com.example.kafkaexmaple.workflow.domain.ProviderType;
import com.example.kafkaexmaple.workflow.domain.ResourceType;
import java.util.Map;

public record WorkflowOperationDetails(
        String name,
        String type,
        Long contractId,
        Long productId,
        Long providerId,
        ProviderType provider,
        ResourceType resourceType,
        String resourceName,
        Map<String, Object> parameters,
        String region,
        String otherParameters
) {
}
