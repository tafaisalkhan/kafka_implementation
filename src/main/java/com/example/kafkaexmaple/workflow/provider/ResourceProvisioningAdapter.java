package com.example.kafkaexmaple.workflow.provider;

import com.example.kafkaexmaple.workflow.domain.ProviderType;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;

public interface ResourceProvisioningAdapter {

    ProviderType provider();

    ProvisioningOutcome provision(WorkflowEvent event);
}
