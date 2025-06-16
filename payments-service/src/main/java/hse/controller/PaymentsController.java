package hse.controller;

import hse.service.PaymentsService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createAccount(@PathVariable UUID userId) {
        paymentsService.createAccount(userId);
        return ResponseEntity.ok("The account was successfully created for user: " + userId);
    }

    @PostMapping("/top-up/{userId}/{amount}")
    public ResponseEntity<String> topUpBalance(@PathVariable UUID userId, @PathVariable BigDecimal amount) {
        paymentsService.topUpBalance(userId, amount);
        return ResponseEntity.ok("The balance of user " + userId + " has been successfully increased by " + amount);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentsService.getBalance(userId));
    }
}