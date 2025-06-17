package hse.scheduler;

import hse.exception.OrdersException;
import hse.model.OutboxEntity;
import hse.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class OutboxScheduler {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOutboxEvents() {
        log.info("Attempting to process outbox events...");
        List<OutboxEntity> events = outboxRepository.findByProcessedFalseOrderByOccurredOnAsc();

        if (events.isEmpty()) {
            log.info("No unprocessed outbox events found.");
            return;
        }

        for (OutboxEntity event : events) {
            try {
                kafkaTemplate.send("order.created", event.getPayload())
                        .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send outbox event to Kafka: {}", event.getId(), ex);
                    } else {
                        log.info("Successfully sent outbox event to Kafka: {}", event.getId());
                        event.setProcessed(true);
                        outboxRepository.save(event);
                    }
                });
            } catch (Exception e) {
                log.error("Unexpected error while processing outbox event: {}", event.getId(), e);
            }
        }
        log.info("Finished processing outbox events.");
    }
}