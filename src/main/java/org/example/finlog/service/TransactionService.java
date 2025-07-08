package org.example.finlog.service;

import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;
import org.example.finlog.repository.TransactionRepository;
import org.example.finlog.util.UuidGenerator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final UuidGenerator uuidGenerator;


    public TransactionService(TransactionRepository transactionRepository, UserService userService, UuidGenerator uuidGenerator) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.uuidGenerator = uuidGenerator;
    }

    public List<Transaction> getFilteredData(String username, Category category, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        UUID userId = user.getId();
        LocalDateTime registrationDate = userService.getRegistrationDate(userId).atStartOfDay();

        if (endDate == null) endDate = LocalDateTime.now();
        if (startDate == null) startDate = registrationDate;


        if (category == null) {
            return transactionRepository.getFiltered(startDate, endDate);
        }

        return transactionRepository.getFiltered(category, startDate, endDate);
    }

    public void save(String username, TransactionRequest request) {
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Transaction transaction = mapToEntity(request, user);
        transactionRepository.save(user.getId(), transaction);
    }

    public Transaction mapToEntity(TransactionRequest request, User user) {
        return Transaction.builder()
                .id(uuidGenerator.generate())
                .user(user)
                .amount(request.getAmount())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();
    }
}
