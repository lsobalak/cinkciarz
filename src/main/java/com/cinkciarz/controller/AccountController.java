package com.cinkciarz.controller;

import com.cinkciarz.dto.AccountCreationRequest;
import com.cinkciarz.dto.AccountDto;
import com.cinkciarz.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        String apiKey = accountService.createNewAccount(accountCreationRequest);
        return ResponseEntity.status(201).body(Map.of("API key", apiKey));
    }

    @GetMapping
    public ResponseEntity<AccountDto> getAccount(@RequestParam String apiKey) {
        AccountDto accountDto = accountService.getAccountByApiKey(apiKey);
        return ResponseEntity.status(200).body(accountDto);
    }
}
