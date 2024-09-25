package com.cinkciarz.dto;

import com.cinkciarz.model.Currency;
import com.cinkciarz.validator.BalanceValidate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeCurrencyRequest {

    @NotNull(message = "fromCurrency is mandatory")
    private Currency fromCurrency;

    @NotNull(message = "toCurrency mandatory")
    private Currency toCurrency;

    @NotNull(message = "amount is mandatory")
    @Min(value = 0, message = "amount must be at least 0")
    @BalanceValidate
    private Double amount;

}