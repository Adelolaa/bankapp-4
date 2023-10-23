package com.example.bankapp4.service;
import com.example.bankapp4.dto.TransactionDto;
import com.example.bankapp4.entity.Transaction;
import com.example.bankapp4.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionImpl implements TransactionService{

   @Autowired
   TransactionRepo transactionRepo;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepo.save(transaction);
        System.out.println("Transaction saved Successfully");

    }
}
