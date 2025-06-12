package com.example.testtask.controller;

import com.example.testtask.dto.TransferRequest;
import com.example.testtask.dto.TransferResponse;
import com.example.testtask.service.WalletService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/wallet")
    public ResponseEntity<?> doTransfer(@RequestBody @Valid TransferRequest transferRequest) {
        walletService.doTransfer(transferRequest);
        log.info("Successful transaction for the amount: {}. Wallet Id: {}", transferRequest.getAmount(), transferRequest.getWalletId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<TransferResponse> getBalance(@PathVariable UUID walletId) {
        TransferResponse balance = walletService.getBalance(walletId);
        log.info("The amount: {}. Wallet Id: {}", balance.getAmount(), walletId);
        return ResponseEntity.ok(balance);
    }
}
