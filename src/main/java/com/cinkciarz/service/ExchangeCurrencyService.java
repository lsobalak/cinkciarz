package com.cinkciarz.service;

import com.cinkciarz.dao.BalanceRepository;
import com.cinkciarz.dto.AccountDto;
import com.cinkciarz.dto.ExchangeCurrencyRequest;
import com.cinkciarz.dto.ExchangeRatesSeries;
import com.cinkciarz.exception.InsufficientBalanceException;
import com.cinkciarz.exception.InternalErrorException;
import com.cinkciarz.model.Balance;
import com.cinkciarz.model.Currency;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeCurrencyService {

    private final BalanceRepository balanceRepository;
    private final RestTemplate restTemplate;
    private final AccountService accountService;

    @Value("${exchange-usd-rate-api-url}")
    private String exchangeRateApiUrl;

    @Transactional
    public AccountDto exchangeCurrency(String apiKey, @Valid ExchangeCurrencyRequest exchangeCurrencyRequest) {
        Map<Currency, Balance> balancesMap = balanceRepository.findByAccountApiKey(apiKey).stream()
                .collect(Collectors.toMap(Balance::getCurrency, balance -> balance));

        Balance currentBalanceOfFromCurrency = balancesMap.get(exchangeCurrencyRequest.getFromCurrency());
        Balance currentBalanceOfToCurrency = balancesMap.get(exchangeCurrencyRequest.getToCurrency());

        validateSufficientBalance(exchangeCurrencyRequest.getAmount(), balancesMap.get(exchangeCurrencyRequest.getFromCurrency()).getAmount());

        if (exchangeCurrencyRequest.getFromCurrency().equals(Currency.PLN)) {
            currentBalanceOfFromCurrency.setAmount(currentBalanceOfFromCurrency.getAmount().subtract(BigDecimal.valueOf(exchangeCurrencyRequest.getAmount())));
            currentBalanceOfToCurrency.setAmount(currentBalanceOfToCurrency.getAmount().add(BigDecimal.valueOf(exchangeCurrencyRequest.getAmount()).divide(getExchangeRate(), RoundingMode.DOWN)));
        } else {
            currentBalanceOfFromCurrency.setAmount(currentBalanceOfFromCurrency.getAmount().subtract(BigDecimal.valueOf(exchangeCurrencyRequest.getAmount())));
            currentBalanceOfToCurrency.setAmount(currentBalanceOfToCurrency.getAmount().add(BigDecimal.valueOf(exchangeCurrencyRequest.getAmount()).multiply(getExchangeRate())));
        }

        balanceRepository.saveAll(List.of(currentBalanceOfFromCurrency, currentBalanceOfToCurrency));

        return accountService.getAccountByApiKey(apiKey);
    }

    private BigDecimal getExchangeRate() {
        return Optional.ofNullable(restTemplate.getForObject(exchangeRateApiUrl, ExchangeRatesSeries.class))
                .orElseThrow(() -> new InternalErrorException("Failed to get exchange rates"))
                .getRates().stream().findAny().map(ExchangeRatesSeries.Rate::getMid).orElseThrow(() -> new InternalErrorException("Rate is not found"));
    }


    private static void validateSufficientBalance(Double wantedAmountToExchange, BigDecimal fromCurrency) {
        if (fromCurrency.compareTo(BigDecimal.valueOf(wantedAmountToExchange)) < 0) {
            throw new InsufficientBalanceException(String.format("Current balance is %s and it is insufficient to exchange %s", fromCurrency, wantedAmountToExchange));
        }
    }
}
