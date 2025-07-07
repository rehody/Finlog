package org.example.finlog.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.finlog.enums.Category;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionDTO {
    @NotNull
    private BigDecimal amount;
    @Size(max = 150)
    private String description;
    @NotNull
    private Category category;
}
