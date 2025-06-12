package com.example.testtask.controller;

import com.example.testtask.dto.TransferRequest;
import com.example.testtask.dto.TransferResponse;
import com.example.testtask.entity.OperationType;
import com.example.testtask.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    public static Stream<Arguments> addParameterToTest() {
        return Stream.of(
                Arguments.of(new BigDecimal("-2000.00"), OperationType.DEPOSIT),
                Arguments.of(new BigDecimal("-2000.00"), OperationType.WITHDRAW),
                Arguments.of(null, null));
    }

    @ParameterizedTest
    @MethodSource("addParameterToTest")
    public void doTransfer_NegativeAmount_InvalidOperationTypeTest(BigDecimal amount, OperationType type) throws Exception {
        UUID walletId = UUID.randomUUID();
        TransferRequest transferRequest = TransferRequest.builder()
                .walletId(walletId)
                .operationType(type)
                .amount(amount)
                .build();

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transferRequest)))
                .andExpect(status().is(400));
    }

    @Test
    public void doTransfer_ValidRequestTest() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("2000.00");
        TransferRequest transferRequest = TransferRequest.builder()
                .walletId(walletId)
                .operationType(OperationType.DEPOSIT)
                .amount(amount)
                .build();

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transferRequest)))
                .andExpect(status().is(200));
    }

    @Test
    void doTransfer_WITHDRAW_InsufficientFundsTest() throws Exception {
        UUID walletId = UUID.randomUUID();
        TransferRequest request = TransferRequest.builder()
                .walletId(walletId)
                .operationType(OperationType.WITHDRAW)
                .amount(new BigDecimal("1000.00"))
                .build();

        doThrow(new IllegalArgumentException("Insufficient funds to withdraw the amount: 1000.00"))
                .when(walletService)
                .doTransfer(argThat(req ->
                        req.getOperationType() == OperationType.WITHDRAW &&
                                req.getAmount().compareTo(new BigDecimal("500.00")) > 0
                ));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(walletService, times(1)).doTransfer(any(TransferRequest.class));
    }

    @Test
    public void getBalance_SuccessTest() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal expectedBalance = new BigDecimal("2000.00");
        when(walletService.getBalance(walletId)).thenReturn(new TransferResponse(new BigDecimal("2000.00")));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(expectedBalance.doubleValue()));
    }

    @Test
    public void getBalance_NotFoundTest() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletService.getBalance(walletId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

