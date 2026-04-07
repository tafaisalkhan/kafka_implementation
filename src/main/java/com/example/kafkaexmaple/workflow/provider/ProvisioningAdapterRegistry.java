package com.example.kafkaexmaple.workflow.provider;

import com.example.kafkaexmaple.workflow.domain.ProviderType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ProvisioningAdapterRegistry {

    private final Map<ProviderType, ResourceProvisioningAdapter> adapters;

    public ProvisioningAdapterRegistry(List<ResourceProvisioningAdapter> adapters) {
        this.adapters = new EnumMap<>(ProviderType.class);
        for (ResourceProvisioningAdapter adapter : adapters) {
            this.adapters.put(adapter.provider(), adapter);
        }
    }

    public ResourceProvisioningAdapter resolve(ProviderType provider) {
        ResourceProvisioningAdapter adapter = adapters.get(provider);
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
        return adapter;
    }
}
