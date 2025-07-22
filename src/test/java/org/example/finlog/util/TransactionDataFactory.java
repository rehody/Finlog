package org.example.finlog.util;

import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDataFactory {

    public static TransactionRequest sampleTransactionRequest(UUID id) {
        return TransactionRequest.builder()
                .id(id)
                .amount(new BigDecimal("123.45"))
                .description("Test description")
                .category(Category.OTHER)
                .build();
    }

    public static Transaction sampleTransaction(UUID id, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Transaction.builder()
                .id(id)
                .user(user)
                .userId(user.getId())
                .amount(new BigDecimal("123.45"))
                .description("Test description")
                .category(Category.OTHER)
                .transactionDate(now)
                .deleted(false)
                .version(0L)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
