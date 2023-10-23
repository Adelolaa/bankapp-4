package com.example.bankapp4.service;

import com.example.bankapp4.dto.*;
import com.example.bankapp4.entity.Role;
import com.example.bankapp4.entity.User;
import com.example.bankapp4.repository.RoleRepo;
import com.example.bankapp4.repository.UserRepository;
import com.example.bankapp4.service.util.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class  UserServiceImpl implements UserService {
//    inject the repository layer

    private UserRepository userRepository;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private RoleRepo roleRepo;
    private TransactionService transactionService;

    public UserServiceImpl(UserRepository userRepository,TransactionService transactionService, EmailService emailService,PasswordEncoder passwordEncoder,RoleRepo roleRepo) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.transactionService=transactionService;
    }

    //    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    EmailService emailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * creating an acct is saving a new user into the database that is,
         * instantiating a new user or
         * instantiating object of a user
         * Check if the account exist
         */

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .otherName(userRequest.getOtherName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();


        Role roles = roleRepo.findByRoleName("ROLE_USER").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        newUser.setRoles(Collections.singleton(roles));


        User savedUser = userRepository.save(newUser);
        //         Send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account has been successfully created. \nYour account details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder().build().builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }

    @Override
  public LoginDto login(LoginDto loginDto){
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

       SecurityContextHolder.getContext().setAuthentication(authentication);
      return LoginDto.builder()
            .usernameOrEmail(loginDto.getUsernameOrEmail())
               .build();
  }



    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
//check if the provided account number exist in the db
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
//        check if the account exist
        boolean isAccountExits = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExits) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //Save Transaction
        TransactionDto transactionDto =TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount() )
                .build();
        transactionService.saveTransaction(transactionDto);

        EmailDetails message = EmailDetails.builder()
                .recipient(userToCredit.getEmail())
                .subject("AA BANK")
                .messageBody("Txn:Credit" + "\n" + "AC" + "21XXX13" + "\n" + "Amt:" + request.getAmount() +
                        "\n" + "Date" + LocalDateTime.now() + "\n" + "Bal:" + userToCredit.getAccountBalance())
                .build();

        emailService.sendSimpleEmail(message);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getOtherName() + " " + userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);


            EmailDetails message = EmailDetails.builder()
                    .recipient(userToDebit.getEmail())
                    .subject("AA BANK")
                    .messageBody("Txn:Debit" + "\n" + "AC" + "21XXX13" + "\n" + "Amt:" + request.getAmount() +
                            "\n" + "Date" + LocalDateTime.now() + "\n" + "Bal:" + userToDebit.getAccountBalance())
                    .build();

            TransactionDto transactionDto =TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount() )
                    .build();
            transactionService.saveTransaction(transactionDto);

            emailService.sendSimpleEmail(message);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getOtherName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();

        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit(check if it exists)
        //check if the amount I'm debiting is not more than the current bal
        // debit the account
        //get the account to credit
        //credit the account

        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccount = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccount.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                    .accountInfo(null)
                    .build();
        }
        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccount.getFirstName()+" "+sourceAccount.getOtherName()+" "+sourceAccount.getLastName();
        userRepository.save(sourceAccount);
        EmailDetails debitAlert =EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccount.getEmail())
                .messageBody("The sum of"+request.getAmount()+"has been deducted from your account! Your current balance is "+sourceAccount.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);


        User destionationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destionationAccountUser.setAccountBalance(destionationAccountUser.getAccountBalance().add(request.getAmount()));
//        String recipientUserName =destinationAccountUser.getFirstName() + " "+destinationAccountUser.getOtherName() + " "+ destinationAccountUser.getLastName();
          userRepository.save(destionationAccountUser);
        EmailDetails creditAlert =EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(sourceAccount.getEmail())
                .messageBody("The sum of"+request.getAmount()+"has been sent to your account from "+sourceUsername+"."+"\n"+"Your current balance is "+sourceAccount.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);


        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }

}




//    balance enquiry, name enquiry, credit, debit, transfer




