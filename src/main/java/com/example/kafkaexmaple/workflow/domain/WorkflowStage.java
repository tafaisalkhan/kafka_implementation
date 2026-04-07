package com.example.kafkaexmaple.workflow.domain;

public enum WorkflowStage {
    INITIATED,
    QUOTA_RESERVED,
    QUOTA_REJECTED,
    PROVISION_SUCCEEDED,
    PROVISION_FAILED,
    QUOTA_COMMITTED,
    QUOTA_REVERTED,
    USAGE_RECORDED
}
