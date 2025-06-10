package hse.controller;

import hse.service.PaymentsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class PaymentsController {
    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/create/{userId}")
    public String createAccount(@PathVariable UUID userId) {
        paymentsService.createAccount(userId);
        return "The account was successfully created for user: " + userId;
    }

    @PostMapping("/top-up/{userId}/{amount}")
    public String topUpBalance(@PathVariable UUID userId, @PathVariable BigDecimal amount) {
        paymentsService.topUpBalance(userId, amount);
        return "The balance of user " + userId + " has been successfully increased by " + amount;
    }

    @GetMapping("/{userId}")
    public BigDecimal getBalance(@PathVariable UUID userId) {
        return paymentsService.getBalance(userId);
    }
}