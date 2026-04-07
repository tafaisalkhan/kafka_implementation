package com.example.kafkaexmaple.workflow.provider;

import com.example.kafkaexmaple.workflow.domain.ProviderType;
import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HuaweiProvisioningAdapter implements ResourceProvisioningAdapter {

    private static final Logger log = LoggerFactory.getLogger(HuaweiProvisioningAdapter.class);

    @Override
    public ProviderType provider() {
        return ProviderType.HUAWEI;
    }

    @Override
    public ProvisioningOutcome provision(WorkflowEvent event) {
        log.info("huawei provisioning requested workflowId={} resourceType={} resourceName={}",
                event.workflowId(), event.resourceType(), event.resourceName());
        if (event.simulateProvisionFailure()) {
            return new ProvisioningOutcome(false, "huawei provisioning failed");
        }
        return new ProvisioningOutcome(true, "huawei provisioning succeeded");
    }
}
