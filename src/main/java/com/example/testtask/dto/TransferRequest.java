package com.example.testtask.dto;

import com.example.testtask.entity.OperationType;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private UUID walletId;
    private OperationType operationType;
    @Positive(message = "Сумма должна быть положительной")
    private BigDecimal amount;
}
