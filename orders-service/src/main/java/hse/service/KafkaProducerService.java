package hse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.event.OrderCreatedEvent;
import hse.exception.OrdersException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderCreatedEvent(UUID id, UUID userId, BigDecimal amount) {
        try {
            String payload = objectMapper.writeValueAsString(new OrderCreatedEvent(id, userId, amount));
            kafkaTemplate.send("order.created", payload);
        } catch (JsonProcessingException e) {
            throw new OrdersException("Event serialization error", e);
        }
    }
}