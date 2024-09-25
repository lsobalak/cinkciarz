package com.cinkciarz.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class BalanceValidator implements ConstraintValidator<BalanceValidate, Double> {
    private static final Pattern BALANCE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        String toValidate = String.valueOf(value);
        return BALANCE_PATTERN.matcher(toValidate).matches();
    }
}