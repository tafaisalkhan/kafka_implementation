package com.example.kafkaexmaple.workflow.service;

import com.example.kafkaexmaple.workflow.dto.WorkflowEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class WorkflowEventCodec {

    private final ObjectMapper objectMapper;

    public WorkflowEventCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(WorkflowEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize workflow event", ex);
        }
    }

    public WorkflowEvent fromJson(String payload) {
        try {
            return objectMapper.readValue(payload, WorkflowEvent.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize workflow event", ex);
        }
    }
}
