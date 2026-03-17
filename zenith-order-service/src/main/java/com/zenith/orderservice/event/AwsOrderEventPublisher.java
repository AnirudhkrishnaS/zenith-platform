package com.zenith.orderservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

/**
 * Production implementation: publishes order events to AWS EventBridge.
 * Active only when running with spring.profiles.active=aws.
 *
 * EventBridge then routes each event to the right SQS queues
 * based on rules configured in AWS (not in this code).
 */
@Component
@Profile("aws")
public class AwsOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AwsOrderEventPublisher.class);

    private final EventBridgeClient client;
    private final ObjectMapper objectMapper;
    private final String busName;
    private final String source;

    public AwsOrderEventPublisher(
            @Value("${events.eventbridge.bus-name}") String busName,
            @Value("${events.eventbridge.source}") String source,
            @Value("${events.eventbridge.region}") String region,
            ObjectMapper objectMapper) {
        this.busName = busName;
        this.source = source;
        this.objectMapper = objectMapper;
        this.client = EventBridgeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public void publish(OrderEvent event) {
        String detail;
        try {
            detail = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", event, e);
            return;
        }

        PutEventsRequest request = PutEventsRequest.builder()
                .entries(PutEventsRequestEntry.builder()
                        .eventBusName(busName)
                        .source(source)
                        .detailType(event.eventType())
                        .detail(detail)
                        .build())
                .build();

        PutEventsResponse response = client.putEvents(request);

        for (PutEventsResultEntry entry : response.entries()) {
            if (entry.errorCode() != null) {
                log.error("EventBridge publish failed: {} - {}", entry.errorCode(), entry.errorMessage());
            } else {
                log.info("[EVENTBRIDGE] Published {} | eventId={}", event.eventType(), entry.eventId());
            }
        }
    }
}
