package hse.service;

import hse.model.AccountEntity;
import hse.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentsService {
    private final AccountRepository accountRepository;

    public PaymentsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountEntity createAccount(UUID userId) {
        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("The account already exists for user: " + userId);
        }

        AccountEntity account = AccountEntity.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();

        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity topUpBalance(UUID userId, BigDecimal amount) {
        AccountEntity account = accountRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("The " +
                "account does not exist for user: " + userId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount has to be positive");
        }

        account.setBalance(account.getBalance().add(amount));

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID userId) {
        return accountRepository.findByUserId(userId)
                .map(AccountEntity::getBalance)
                .orElseThrow(() -> new RuntimeException("The account already exists for user: " + userId));
    }
}