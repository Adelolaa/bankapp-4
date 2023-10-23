package com.example.bankapp4.controller;

import com.example.bankapp4.dto.*;
import com.example.bankapp4.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Account Management Apis")
public class UserController {

   @Autowired
   UserService userService;




    @ApiResponse(
            responseCode = "200",
            description = "http status 200 success"
    )

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user and assigning an account ID"
    )
    @PostMapping("/create")
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }


    @Operation(
            summary = "Bal Enquiry",
            description = "Given an account number, check how much the user has"
    )

    @ApiResponse(
            responseCode = "201",
            description = "http status 201 CREATED"
    )

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }
    @PostMapping("/signin")
    public LoginDto login(@RequestBody LoginDto loginDto) {
       return userService.login(loginDto);
    }

    @ApiResponse(
            responseCode = "202",
            description = "http status 202 success"
    )

    @Operation(
            summary = "Name Enquiry",
            description = "Checking if a user exists using account number"
    )

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @ApiResponse(
            responseCode = "203",
            description = "http status 203 success"
    )

    @Operation(
            summary = "Credit Transaction",
            description = "Credit a user account"
    )
    @PostMapping("/user/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @ApiResponse(
            responseCode = "204",
            description = "http status 204 success"
    )

    @Operation(
            summary = "Debit Transaction",
            description = "Debit a user account"
    )
    @PostMapping("/all/api/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

 @ApiResponse(
         responseCode = "205",
         description = "http status 205 success"
 )

 @Operation(
         summary = "Transfer",
         description = "Make a Transfer Using Account Number "
 )
    @PostMapping("/user/all/api/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }






}
