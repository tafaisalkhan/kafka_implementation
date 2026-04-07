package com.example.kafkaexmaple.workflow.controller;

import com.example.kafkaexmaple.workflow.dto.WorkflowInitiationRequest;
import com.example.kafkaexmaple.workflow.dto.WorkflowIdResponse;
import com.example.kafkaexmaple.workflow.service.WorkflowWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowWorkflowService workflowService;

    public WorkflowController(WorkflowWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public WorkflowIdResponse initiate(@Valid @RequestBody WorkflowInitiationRequest request) {
        return workflowService.initiate(request);
    }
}
