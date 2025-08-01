package org.example.finlog.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.DTO.TransactionResponse;
import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.util.TransactionMapper;
import org.example.finlog.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getFiltered(
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate,
            Principal principal
    ) {
        String username = principal.getName();
        List<Transaction> transactions = transactionService
                .getFilteredData(
                        username,
                        category,
                        startDate,
                        endDate
                );

        log.debug("Fetching transactions for user {}, category={}, startDate={}, endDate={}",
                username, category, startDate, endDate);

        List<TransactionResponse> responseList = transactions
                .stream()
                .map(TransactionMapper::mapToDto)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getSingle(
            @PathVariable UUID id,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        Transaction transaction = transactionService.getSingle(username, id);
        TransactionResponse response = TransactionMapper.mapToDto(transaction);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody TransactionRequest request,
            Principal principal
    ) {
        String username = principal.getName();
        transactionService.save(username, request);
        log.debug("Transaction created for user: {}", username);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(
            @Valid @RequestBody TransactionRequest request,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        transactionService.update(username, request);
        log.debug("Transaction updated: {}", request.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        transactionService.delete(username, id);
        log.debug("Transaction deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}
