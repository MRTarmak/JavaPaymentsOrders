package hse.service;

import hse.dto.OrderDto;
import hse.event.OrderCreatedEvent;
import hse.model.OrderEntity;
import hse.model.OrderStatus;
import hse.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersService {
    private final OrderRepository orderRepository;

    private final KafkaProducerService kafkaProducerService;

    public OrdersService(OrderRepository orderRepository, KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public OrderDto createOrder(UUID userId, BigDecimal amount, String description) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Illegal data");
        }

        UUID orderId;

        boolean idExists;
        do {
            orderId = UUID.randomUUID();
            idExists = orderRepository.existsById(orderId);
        } while (idExists);

        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .userId(userId)
                .amount(amount)
                .description(description)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now())
                .build();

        OrderEntity savedOrder = orderRepository.save(order);

        kafkaProducerService.sendOrderCreatedEvent(savedOrder.getId(), savedOrder.getUserId(), savedOrder.getAmount());

        return OrderDto.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .amount(savedOrder.getAmount())
                .description(savedOrder.getDescription())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .updatedAt(savedOrder.getUpdatedAt())
                .build();
    }

    @Transactional
    public List<OrderDto> viewOrdersList() {
        return orderRepository.findAll().stream().map(order -> OrderDto.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .amount(order.getAmount())
                    .description(order.getDescription())
                    .status(order.getStatus())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .build()
        ).toList();
    }

    // TODO viewOrderStatus, viewOrdersListByUserId
}
