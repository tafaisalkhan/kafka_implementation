package com.example.kafkaexmaple.workflow.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class WorkflowReservationStore {

    private final Map<String, Boolean> reservations = new ConcurrentHashMap<>();

    public void reserve(String workflowId) {
        reservations.put(workflowId, true);
    }

    public boolean isReserved(String workflowId) {
        return reservations.getOrDefault(workflowId, false);
    }

    public void release(String workflowId) {
        reservations.remove(workflowId);
    }
}
