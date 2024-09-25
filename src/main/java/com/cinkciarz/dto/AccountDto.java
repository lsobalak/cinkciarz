package com.cinkciarz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String username;

    private String firstName;

    private String lastName;

    private String apiKey;

    private Set<BalanceResponse> balances;

}