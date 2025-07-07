package org.example.finlog.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.finlog.DTO.TransactionDTO;
import org.example.finlog.entity.Transaction;
import org.example.finlog.enums.Category;
import org.example.finlog.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Principal principal
    ) {
        try {
            String username = principal.getName();
            List<Transaction> transactions = transactionService.getFilteredData(username, category, startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(
            @Valid @RequestBody TransactionDTO dto,
            Principal principal
    ) {
        try {
            String username = principal.getName();
            transactionService.save(username, dto);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error saving transaction", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

}
