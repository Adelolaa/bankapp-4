package com.example.bankapp4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginDto {

        private String usernameOrEmail;
        private  String password;
}
