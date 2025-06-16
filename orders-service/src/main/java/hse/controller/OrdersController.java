package hse.controller;

import hse.dto.OrderDto;
import hse.model.OrderStatus;
import hse.service.OrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/orders")
public class OrdersController {
    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping("/create/{userId}/{amount}/{description}")
    public ResponseEntity<OrderDto> createOrder(@PathVariable UUID userId,
                                                @PathVariable BigDecimal amount,
                                                @PathVariable String description) {
        return ResponseEntity.ok(ordersService.createOrder(userId, amount, description));
    }

    @GetMapping("/view")
    public ResponseEntity<List<OrderDto>> viewOrdersList() {
        return ResponseEntity.ok(ordersService.viewOrdersList());
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<List<OrderDto>> viewOrdersListByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(ordersService.viewOrdersListByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatus> viewOrderStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(ordersService.viewOrderStatus(id));
    }
}
