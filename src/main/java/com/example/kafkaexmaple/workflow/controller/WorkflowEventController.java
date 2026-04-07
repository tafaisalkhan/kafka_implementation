package com.example.kafkaexmaple.workflow.controller;

import com.example.kafkaexmaple.workflow.store.WorkflowEventStore;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows/events")
public class WorkflowEventController {

    private final WorkflowEventStore eventStore;

    public WorkflowEventController(WorkflowEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @GetMapping
    public List<WorkflowEventStore.StoredWorkflowEvent> events() {
        return eventStore.getAll();
    }
}
