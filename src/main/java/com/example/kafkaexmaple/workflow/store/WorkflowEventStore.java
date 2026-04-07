package com.example.kafkaexmaple.workflow.store;

import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Service;

@Service
public class WorkflowEventStore {

    public record StoredWorkflowEvent(String actor, String topic, WorkflowEvent event) {
    }

    private final ConcurrentLinkedQueue<StoredWorkflowEvent> events = new ConcurrentLinkedQueue<>();

    public void add(String actor, String topic, WorkflowEvent event) {
        events.add(new StoredWorkflowEvent(actor, topic, event));
    }

    public List<StoredWorkflowEvent> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }
}
