package org.example.finlog.service;

import org.example.finlog.DTO.TransactionDTO;
import org.example.finlog.entity.Transaction;
import org.example.finlog.entity.User;
import org.example.finlog.enums.Category;
import org.example.finlog.repository.TransactionRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;


    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public List<Transaction> getFilteredData(String username, Category category, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        UUID userId = user.getId();
        LocalDate registrationDate = userService.getRegistrationDate(userId);

        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = registrationDate;


        if (category == null) {
            return transactionRepository.getFiltered(startDate, endDate);
        }

        return transactionRepository.getFiltered(category, startDate, endDate);
    }

    public void save(String username, TransactionDTO dto) {
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));


        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setCategory(dto.getCategory());

        transactionRepository.save(user.getId(), transaction);
    }
}
