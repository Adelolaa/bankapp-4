package com.example.bankapp4.service;

import com.example.bankapp4.dto.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
