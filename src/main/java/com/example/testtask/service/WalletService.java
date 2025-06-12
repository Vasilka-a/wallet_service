package com.example.testtask.service;

import com.example.testtask.dto.TransferRequest;
import com.example.testtask.dto.TransferResponse;
import com.example.testtask.entity.Operation;
import com.example.testtask.exceptions.DataHasNotUpdateException;
import com.example.testtask.repository.OperationRepository;
import com.example.testtask.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

@org.springframework.stereotype.Service

public class WalletService {
    private final WalletRepository walletRepository;
    private final OperationRepository operationRepository;

    public WalletService(WalletRepository walletRepository, OperationRepository operationRepository) {
        this.walletRepository = walletRepository;
        this.operationRepository = operationRepository;
    }

    public void doTransfer(TransferRequest transferRequest) {
        BigDecimal actualBalance = getBalance(transferRequest.getWalletId()).getAmount();

        switch (transferRequest.getOperationType()) {
            case DEPOSIT -> deposit(actualBalance, transferRequest);
            case WITHDRAW -> {
                if (actualBalance.compareTo(transferRequest.getAmount()) < 0) {
                    saveOperation(transferRequest, false);
                    throw new IllegalArgumentException("Insufficient funds to withdraw the amount: " + transferRequest.getAmount());//недостаточно средств
                }
                withdraw(actualBalance, transferRequest);
            }
            default -> {
                saveOperation(transferRequest, false);
                throw new IllegalArgumentException("Unexpected operation type: " + transferRequest.getOperationType());
            }
        }
    }

    public TransferResponse getBalance(UUID walletId) {
        BigDecimal amount = walletRepository.getWalletByUUID(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet is not found. Wallet Id: " + walletId));
        return new TransferResponse(amount);
    }


    public void deposit(BigDecimal actualBalance, TransferRequest transferRequest) {
        int success = walletRepository.updateAmountById(actualBalance.add(transferRequest.getAmount()), transferRequest.getWalletId());
        if (success == 0) {
            saveOperation(transferRequest, false);
            throw new DataHasNotUpdateException("The data has not been update. Wallet Id: " + transferRequest.getWalletId());
        }
        saveOperation(transferRequest, true);
    }

    public void withdraw(BigDecimal actualBalance, TransferRequest transferRequest) {
        int success = walletRepository.updateAmountById((actualBalance.subtract(transferRequest.getAmount())), transferRequest.getWalletId());
        if (success == 0) {
            saveOperation(transferRequest, false);
            throw new DataHasNotUpdateException("The data has not been update. Wallet Id: " + transferRequest.getWalletId());
        }
        saveOperation(transferRequest, true);
    }

    public void saveOperation(TransferRequest transferRequest, Boolean success) {
        Operation operation = Operation.builder()
                .walletId(transferRequest.getWalletId())
                .operationType(transferRequest.getOperationType())
                .amount(transferRequest.getAmount())
                .success(success)
                .build();
        operationRepository.save(operation);
    }
}
