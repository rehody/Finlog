package org.example.finlog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.service.TransactionService;
import org.example.finlog.util.ApiTag;
import org.springframework.format.annotation.DateTimeFormat;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = ApiTag.TRANSACTIONS, description = "Financial transaction management")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Get filtered transactions",
            description = "Returns transactions with optional filters by category and date range",
            parameters = {
                    @Parameter(name = "category", description = "Transaction category", example = "FAST_FOOD"),
                    @Parameter(name = "startDate", description = "Start date (ISO format)", example = "2025-07-20"),
                    @Parameter(name = "endDate", description = "End date (ISO format)", example = "2026-01-01")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered transactions"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
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
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Create transaction",
            description = "Creates a new financial transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
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

    @Operation(
            summary = "Update transaction",
            description = "Updates an existing transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied for this user"),
                    @ApiResponse(responseCode = "404", description = "Transaction or user not found")
            })
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

    @Operation(
            summary = "Delete transaction",
            description = "Deletes a transaction by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied for this user"),
                    @ApiResponse(responseCode = "404", description = "Transaction or user not found")

            })
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

