package com.zenith.orderservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Local-dev implementation: logs events to console instead of sending to AWS.
 * Active when the "aws" profile is NOT set (default for local development).
 *
 * Run with: mvn spring-boot:run                         → this bean is used
 * Run with: mvn spring-boot:run -Dspring.profiles.active=aws → AwsOrderEventPublisher is used
 */
@Component
@Profile("!aws")
public class LoggingOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingOrderEventPublisher.class);

    @Override
    public void publish(OrderEvent event) {
        log.info("[EVENT] {} | orderId={} storeId={} customerId={} status={} previousStatus={} total={}",
                event.eventType(),
                event.orderId(),
                event.storeId(),
                event.customerId(),
                event.status(),
                event.previousStatus(),
                event.total());
    }
}
