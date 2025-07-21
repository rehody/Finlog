package org.example.finlog.mapper;

import org.example.finlog.DTO.TransactionResponse;
import org.example.finlog.entity.Transaction;

import java.time.temporal.ChronoUnit;

public class TransactionMapper {
    public static TransactionResponse mapToDto(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory(),
                transaction.getTransactionDate().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
