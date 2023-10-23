package com.example.bankapp4.service;

import com.example.bankapp4.dto.*;

public interface UserService {

   BankResponse createAccount(UserRequest userRequest);
   LoginDto login(LoginDto loginDto);
   BankResponse balanceEnquiry(EnquiryRequest request);
   String nameEnquiry(EnquiryRequest request);
   BankResponse creditAccount(CreditDebitRequest request);
   BankResponse debitAccount(CreditDebitRequest request);
   BankResponse transfer(TransferRequest request);

}
