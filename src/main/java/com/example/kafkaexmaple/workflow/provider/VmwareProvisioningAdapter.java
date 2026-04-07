package com.example.kafkaexmaple.workflow.provider;

import com.example.kafkaexmaple.workflow.domain.ProviderType;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VmwareProvisioningAdapter implements ResourceProvisioningAdapter {

    private static final Logger log = LoggerFactory.getLogger(VmwareProvisioningAdapter.class);

    @Override
    public ProviderType provider() {
        return ProviderType.VMWARE;
    }

    @Override
    public ProvisioningOutcome provision(WorkflowEvent event) {
        log.info("vmware provisioning requested workflowId={} resourceType={} resourceName={}",
                event.workflowId(), event.resourceType(), event.resourceName());
        if (event.simulateProvisionFailure()) {
            return new ProvisioningOutcome(false, "vmware provisioning failed");
        }
        return new ProvisioningOutcome(true, "vmware provisioning succeeded");
    }
}
