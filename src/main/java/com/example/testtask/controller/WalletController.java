package com.example.testtask.controller;

import com.example.testtask.dto.TransferRequest;
import com.example.testtask.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

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
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }
}
