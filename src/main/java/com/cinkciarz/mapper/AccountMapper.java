package com.cinkciarz.mapper;

import com.cinkciarz.dto.AccountCreationRequest;
import com.cinkciarz.dto.AccountDto;
import com.cinkciarz.model.Account;
import com.cinkciarz.model.Balance;
import com.cinkciarz.model.Currency;
import org.apache.commons.lang3.RandomStringUtils;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface AccountMapper {

    @Mapping(source = "initialAmountInPln", target = "balances", qualifiedByName = "initialAmountInPlnToInitialBalance")
    Account mapToAccount(AccountCreationRequest accountCreationRequest);

    AccountDto mapToAccountResponse(Account account);

    @AfterMapping
    default void generateApiKey(@MappingTarget Account.AccountBuilder accountBuilder) {
        accountBuilder.apiKey(RandomStringUtils.randomAlphanumeric(10));
    }

    @Named("initialAmountInPlnToInitialBalance")
    default Set<Balance> mapInitialAmountInPlnToInitialBalance(double initialAmountInPln) {
        return Set.of(
                Balance.builder().amount(BigDecimal.valueOf(initialAmountInPln)).currency(Currency.PLN).build(),
                Balance.builder().amount(BigDecimal.ZERO).currency(Currency.DOL).build()
        );
    }
}