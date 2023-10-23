package com.example.bankapp4.repository;

import com.example.bankapp4.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, String> {

}
