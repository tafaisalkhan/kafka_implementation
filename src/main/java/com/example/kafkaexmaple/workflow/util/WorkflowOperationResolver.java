package com.example.kafkaexmaple.workflow.util;

import com.example.kafkaexmaple.workflow.domain.ProviderType;
import com.example.kafkaexmaple.workflow.domain.ResourceType;
import com.example.kafkaexmaple.workflow.dto.WorkflowOperationDetails;
import com.example.kafkaexmaple.workflow.dto.WorkflowOperationRequest;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkflowOperationResolver {

    public WorkflowOperationDetails resolve(WorkflowOperationRequest request) {
        ProviderType provider = resolveProvider(request.name());
        ResourceType resourceType = resolveResourceType(request.name());
        String resourceName = extractResourceName(request.parameters(), request.name());
        return new WorkflowOperationDetails(
                request.name(),
                request.type(),
                request.contractId(),
                request.productId(),
                request.providerId(),
                provider,
                resourceType,
                resourceName,
                request.parameters(),
                request.region(),
                request.otherParameters()
        );
    }

    private ProviderType resolveProvider(String operationName) {
        String normalized = operationName.toLowerCase();
        if (normalized.startsWith("os_") || normalized.contains("openstack")) {
            return ProviderType.OPENSTACK;
        }
        if (normalized.contains("vmware")) {
            return ProviderType.VMWARE;
        }
        if (normalized.contains("huawei")) {
            return ProviderType.HUAWEI;
        }
        throw new IllegalArgumentException("Cannot resolve provider from operation name: " + operationName);
    }

    private ResourceType resolveResourceType(String operationName) {
        String normalized = operationName.toLowerCase();
        if (normalized.contains("vm")) {
            return ResourceType.VM;
        }
        if (normalized.contains("storage") || normalized.contains("volume")) {
            return ResourceType.STORAGE;
        }
        if (normalized.contains("floating") || normalized.contains("fip")) {
            return ResourceType.FLOATING_IP;
        }
        throw new IllegalArgumentException("Cannot resolve resource type from operation name: " + operationName);
    }

    private String extractResourceName(Map<String, Object> parameters, String fallback) {
        Object name = parameters.get("name");
        if (name != null && !name.toString().isBlank()) {
            return name.toString();
        }
        return fallback;
    }
}
