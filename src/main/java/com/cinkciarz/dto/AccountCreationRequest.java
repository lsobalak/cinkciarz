package com.cinkciarz.dto;

import com.cinkciarz.validator.BalanceValidate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountCreationRequest {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "Initial amount in PLN is mandatory")
    @Min(value = 0, message = "Initial amount in PLN must be at least 0")
    @BalanceValidate
    private Double initialAmountInPln;
}