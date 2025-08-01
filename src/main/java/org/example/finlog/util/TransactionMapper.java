package org.example.finlog.util;

import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.DTO.TransactionResponse;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;

import java.time.temporal.ChronoUnit;

public class TransactionMapper {
    public static TransactionResponse mapToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .category(transaction.getCategory())
                .transactionDate(transaction.getTransactionDate()
                        .truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    public static Transaction mapToEntity(TransactionRequest request, User user) {
        return Transaction.builder()
                .id(request.getId())
                .user(user)
                .userId(user.getId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .category(request.getCategory())
                .transactionDate(request.getTransactionDate())
                .build();
    }
}
