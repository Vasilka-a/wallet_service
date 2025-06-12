package com.example.testtask.dto;

import com.example.testtask.entity.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
public class TransferRequest {
    @NotNull(message = "WalletId cannot be null")
    private UUID walletId;

    @NotNull(message = "Operation type cannot be null")
    private OperationType operationType;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "The amount must be positive")
    private BigDecimal amount;
}
