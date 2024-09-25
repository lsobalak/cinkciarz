package com.cinkciarz.controller;

import com.cinkciarz.dto.AccountDto;
import com.cinkciarz.dto.ExchangeCurrencyRequest;
import com.cinkciarz.service.ExchangeCurrencyService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/exchange")
@AllArgsConstructor
public class CurrencyExchangeController {

    private final ExchangeCurrencyService exchangeCurrencyService;

    @PostMapping
    @Transactional
    public ResponseEntity<AccountDto> exchangeCurrency(@RequestParam String apiKey, @Valid @RequestBody ExchangeCurrencyRequest exchangeCurrencyRequest) {
        AccountDto accountDto = exchangeCurrencyService.exchangeCurrency(apiKey, exchangeCurrencyRequest);
        return ResponseEntity.status(200).body(accountDto);
    }
}
