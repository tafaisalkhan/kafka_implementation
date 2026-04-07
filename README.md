# Kafka Implementation

This project now contains two Kafka demos:

- the original simple producer/consumer sample
- a saga-style resource provisioning workflow for VM, storage, and floating IP

The workflow flow is the important part:

- API creates a `workflowId`, stores the first event in memory, and publishes `resource-workflow-initiated` asynchronously
- quota service reserves or rejects quota
- provisioning service resolves the provider adapter after reservation
- success publishes quota-commit and usage events
- failure publishes quota-revert events

Supported resource types:

- `VM`
- `STORAGE`
- `FLOATING_IP`

## Requirements

- Java 21
- Maven 3.8+
- Kafka reachable at `localhost:9092`

## Run Kafka in Docker

Make sure the broker advertises `localhost:9092` to the host machine.
If your Docker setup uses a different port or hostname, update:

- `src/main/resources/application.yml`

## Start the app

```bash
mvn spring-boot:run
```

## Simple demo curl

```bash
curl -X POST http://localhost:8080/api/producer/messages \
  -H 'Content-Type: application/json' \
  -d '{"topic":"topic-one","message":"hello kafka"}'
```

## Workflow curl

Success case:

```bash
curl -X POST http://localhost:8080/api/workflows \
  -H 'Content-Type: application/json' \
  -d '{
    "operation":{
      "name":"os_create_vm",
      "type":"instance_creating",
      "contractId":1169,
      "productId":2,
      "providerId":33,
      "parameters":{
        "name":"rebuiltMain",
        "password":"",
        "flavorRef":"2d6efff7-3ede-4382-9a98-ac80d7023b19",
        "imageRef":"7e21d3e2-e5e3-4a1f-b61e-4d3d93125399",
        "block_device_mapping_v2":[{"boot_index":0,"destination_type":"volume","source_type":"image","uuid":"7e21d3e2-e5e3-4a1f-b61e-4d3d93125399","volume_size":20,"volume_type":"ce6d88d0-cae5-430a-b0dd-215de1d6f79d","updatedSize":20,"productVolumeType":"9072"}],
        "metadata":{},
        "networks":[{"uuid":"0b6fb720-e2ef-46ae-a849-eaead8a8063b"}],
        "mode":"quickStart",
        "security_groups":[],
        "user_data":"",
        "availability_zone":"nova",
        "project_id":"12e2844e83d343b9be88bce89866b6d0",
        "productImageRef":"9069",
        "productFlavorRef":"9070",
        "billingMode":"payg"
      },
      "region":"California",
      "otherParameters":"{}"
    },
    "simulateProvisionFailure":false,
    "simulateQuotaFailure":false
  }'
```

Failure case:

```bash
curl -X POST http://localhost:8080/api/workflows \
  -H 'Content-Type: application/json' \
  -d '{
    "operation":{
      "name":"os_create_vm",
      "type":"instance_creating",
      "contractId":1169,
      "productId":2,
      "providerId":33,
      "parameters":{
        "name":"rebuiltMain",
        "password":"",
        "flavorRef":"2d6efff7-3ede-4382-9a98-ac80d7023b19",
        "imageRef":"7e21d3e2-e5e3-4a1f-b61e-4d3d93125399",
        "block_device_mapping_v2":[{"boot_index":0,"destination_type":"volume","source_type":"image","uuid":"7e21d3e2-e5e3-4a1f-b61e-4d3d93125399","volume_size":20,"volume_type":"ce6d88d0-cae5-430a-b0dd-215de1d6f79d","updatedSize":20,"productVolumeType":"9072"}],
        "metadata":{},
        "networks":[{"uuid":"0b6fb720-e2ef-46ae-a849-eaead8a8063b"}],
        "mode":"quickStart",
        "security_groups":[],
        "user_data":"",
        "availability_zone":"nova",
        "project_id":"12e2844e83d343b9be88bce89866b6d0",
        "productImageRef":"9069",
        "productFlavorRef":"9070",
        "billingMode":"payg"
      },
      "region":"California",
      "otherParameters":"{}"
    },
    "simulateProvisionFailure":true,
    "simulateQuotaFailure":false
  }'
```

Both calls return the same response shape:

```json
{
  "workflowId": "uuid"
}
```

## Inspect workflow events

```bash
curl http://localhost:8080/api/workflows/events
```

## Flow Diagram

See [`WORKFLOW_FLOW.md`](WORKFLOW_FLOW.md) for the full event sequence and class map.

If you want a visual file, open [`WORKFLOW_FLOW.svg`](WORKFLOW_FLOW.svg).

If you want plain text, open [`WORKFLOW_FLOW_ASCII.txt`](WORKFLOW_FLOW_ASCII.txt).

## Inspect the old demo consumer store

```bash
curl http://localhost:8080/api/consumer/messages
curl "http://localhost:8080/api/consumer/messages?topic=example-topic"
```

## Notes

- The workflow uses a generic event envelope, so the same pattern works for VM, storage, and floating IP.
- The workflow request includes `operation`, and the provider is inferred from `operation.name`, so OpenStack, VMware, and Huawei share one orchestration path.
- `WorkflowEventStore` is an in-memory trace only. It records every emitted and consumed event for debugging and `/api/workflows/events`, but Kafka remains the real workflow transport.
- The initiation endpoint returns only `{"workflowId":"..."}` and the Kafka publish is dispatched asynchronously.
- For a production system, add idempotency keys, retries, dead-letter topics, and a durable outbox table.
