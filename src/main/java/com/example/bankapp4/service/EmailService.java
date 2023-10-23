package com.example.bankapp4.service;

import com.example.bankapp4.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    String sendSimpleEmail(EmailDetails emailDetails);



}
