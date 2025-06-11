package com.example.testtask.service;

import com.example.testtask.dto.TransferRequest;
import com.example.testtask.exceptions.DataHasNotUpdateException;
import com.example.testtask.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@org.springframework.stereotype.Service
@Transactional
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void doTransfer(TransferRequest transferRequest) {
        BigDecimal actualBalance = getBalance(transferRequest.getWalletId());

        switch (transferRequest.getOperationType()) {
            case DEPOSIT ->
                    deposit(actualBalance, transferRequest.getAmount(), transferRequest.getWalletId());//внести, если есть обновить сумму
            case WITHDRAW -> {
                if (actualBalance.compareTo(transferRequest.getAmount()) < 0) {
                    throw new IllegalArgumentException("Insufficient funds to withdraw the amount: " + transferRequest.getAmount());//недостаточно средств
                }
                withdraw(actualBalance, transferRequest.getAmount(), transferRequest.getWalletId());//снять, если больше вывести ошибку
            }
            default ->
                    throw new IllegalArgumentException("Unexpected operation type: " + transferRequest.getOperationType());
        }
    }

    public BigDecimal getBalance(UUID walletId) {
        return walletRepository.getWalletByUUID(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet is not found"));
    }

    public void deposit(BigDecimal actualBalance, BigDecimal amount, UUID walletId) {
        if (walletRepository.makeDeposit(actualBalance.add(amount), walletId) == 0) {
            throw new DataHasNotUpdateException("The data has not been update");
        }
    }

    public void withdraw(BigDecimal actualBalance, BigDecimal amount, UUID walletId) {
        if (walletRepository.makeWithdraw((actualBalance.subtract(amount)), walletId) == 0) {
            throw new DataHasNotUpdateException("The data has not been update");
        }
    }
}
