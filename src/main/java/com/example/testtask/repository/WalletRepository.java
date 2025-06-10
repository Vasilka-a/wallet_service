package com.example.testtask.repository;

import com.example.testtask.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query("SELECT w.amount FROM Wallet w WHERE w.walletId = :walletId")
    BigDecimal getWalletByUUID(UUID walletId);

    @Modifying
    @Query("UPDATE Wallet w SET w.amount = :actualAmount where w.walletId = :walletId ")
    int makeDeposit(BigDecimal actualAmount, UUID walletId);

    @Modifying
    @Query("UPDATE Wallet w SET w.amount = :actualAmount where w.walletId = :walletId ")
    int makeWithdraw(BigDecimal actualAmount, UUID walletId);
}

