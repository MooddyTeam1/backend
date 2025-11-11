package com.moa.backend.domain.wallet.entity;

public enum WalletTransactionType {
    DEPOSIT,
    WITHDRAW,
    SETTLEMENT_FIRST,
    SETTLEMENT_FINAL,
    REFUND_DEBIT,
    ADJUSTMENT
}

