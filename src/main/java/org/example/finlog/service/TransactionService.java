package org.example.finlog.service;

import org.example.finlog.DTO.TransactionRequest;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;
import org.example.finlog.exception.NotFoundException;
import org.example.finlog.mapper.TransactionMapper;
import org.example.finlog.repository.TransactionRepository;
import org.example.finlog.util.UuidGenerator;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        User user = getUser(username);
        UUID userId = user.getId();

        if (endDate == null) endDate = LocalDateTime.now();
        if (startDate == null) startDate = LocalDateTime.MIN;

        if (endDate.isBefore(startDate)) {
            return List.of();
        }

        return transactionRepository.getFiltered(userId, category, startDate, endDate);
    }

    public Transaction getSingle(String username, UUID id) throws AccessDeniedException {
        User user = getUser(username);
        Transaction transaction = getTransaction(id);

        checkOwnership(user, transaction);
        return transaction;
    }

    public void save(String username, TransactionRequest request) {
        User user = getUser(username);

        if (request.getId() == null) {
            request.setId(uuidGenerator.generate());
        }

        Transaction transaction = TransactionMapper.mapToEntity(request, user);
        transactionRepository.save(transaction);
    }

    public void update(String username, TransactionRequest request) throws AccessDeniedException {
        User user = getUser(username);
        Transaction existing = getTransaction(request.getId());

        checkOwnership(user, existing);
        Transaction transaction = TransactionMapper.mapToEntity(request, user);
        transactionRepository.update(transaction);
    }

    public void delete(String username, UUID id) throws AccessDeniedException {
        User user = getUser(username);
        Transaction existing = getTransaction(id);

        checkOwnership(user, existing);
        transactionRepository.delete(id, existing.getVersion());
    }

    private void checkOwnership(User user, Transaction transaction) throws AccessDeniedException {
        if (!transaction.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private User getUser(String username) {
        return userService.getUserByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    private Transaction getTransaction(UUID request) {
        return Optional.ofNullable(transactionRepository.getById(request))
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
    }
}
