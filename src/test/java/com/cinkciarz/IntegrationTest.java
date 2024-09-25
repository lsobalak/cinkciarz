package com.cinkciarz;

import com.cinkciarz.dto.AccountCreationRequest;
import com.cinkciarz.dto.AccountDto;
import com.cinkciarz.dto.BalanceResponse;
import com.cinkciarz.dto.ExchangeCurrencyRequest;
import com.cinkciarz.model.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void integrationTest() throws Exception {
        //Create user
        AccountCreationRequest accountCreationRequest = AccountCreationRequest.builder()
                .firstName("Lukasz")
                .lastName("Sobalak")
                .username("Username")
                .initialAmountInPln(666.66)
                .build();
        String apiKey = (String) objectMapper.readValue(mockMvc.perform(post("/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountCreationRequest)))
                .andReturn().getResponse().getContentAsString(), Map.class).get("API key");
        assertThat(apiKey).isNotBlank();

        //Get Created user's account
        AccountDto accountDto = objectMapper.readValue(mockMvc.perform(get("/v1/account?apiKey=" + apiKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString(), AccountDto.class);

        assertThat(accountDto.getApiKey()).isEqualTo(apiKey);
        assertThat(accountDto.getFirstName()).isEqualTo("Lukasz");
        assertThat(accountDto.getLastName()).isEqualTo("Sobalak");
        assertThat(accountDto.getUsername()).isEqualTo("Username");
        assertThat(accountDto.getBalances().stream().filter(balance -> Currency.PLN.equals(balance.getCurrency())).map(BalanceResponse::getAmount).findAny().get()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(666.66));
        assertThat(accountDto.getBalances().stream().filter(balance -> Currency.DOL.equals(balance.getCurrency())).map(BalanceResponse::getAmount).findAny().get()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.ZERO);

        //Exchange PLN -> USD
        ExchangeCurrencyRequest exchangeCurrencyPLNtoUSDRequest = ExchangeCurrencyRequest.builder().fromCurrency(Currency.PLN).toCurrency(Currency.DOL).amount(100.66).build();
        AccountDto accountDtoAfterExchangeToUSD = objectMapper.readValue(mockMvc.perform(post("/v1/exchange?apiKey=" + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchangeCurrencyPLNtoUSDRequest)))
                .andReturn().getResponse().getContentAsString(), AccountDto.class);

        assertThat(accountDtoAfterExchangeToUSD.getApiKey()).isEqualTo(apiKey);
        assertThat(accountDtoAfterExchangeToUSD.getFirstName()).isEqualTo("Lukasz");
        assertThat(accountDtoAfterExchangeToUSD.getLastName()).isEqualTo("Sobalak");
        assertThat(accountDtoAfterExchangeToUSD.getUsername()).isEqualTo("Username");
        assertThat(accountDtoAfterExchangeToUSD.getBalances().stream().filter(balance -> Currency.PLN.equals(balance.getCurrency())).map(BalanceResponse::getAmount).findAny().get()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(666.66).subtract(BigDecimal.valueOf(exchangeCurrencyPLNtoUSDRequest.getAmount())));
        assertThat(accountDtoAfterExchangeToUSD.getBalances().stream().filter(balance -> Currency.DOL.equals(balance.getCurrency())).map(BalanceResponse::getAmount).findAny().get()).usingComparator(BigDecimal::compareTo).isGreaterThan(BigDecimal.ZERO);

        //Exchange USD -> PLN
        ExchangeCurrencyRequest exchangeCurrencyUSDtoPLNRequest = ExchangeCurrencyRequest.builder().fromCurrency(Currency.DOL).toCurrency(Currency.PLN).amount(5.0).build();
        AccountDto accountDtoAfterExchangeToPLN = objectMapper.readValue(mockMvc.perform(post("/v1/exchange?apiKey=" + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchangeCurrencyUSDtoPLNRequest)))
                .andReturn().getResponse().getContentAsString(), AccountDto.class);

        assertThat(accountDtoAfterExchangeToPLN.getApiKey()).isEqualTo(apiKey);
        assertThat(accountDtoAfterExchangeToPLN.getFirstName()).isEqualTo("Lukasz");
        assertThat(accountDtoAfterExchangeToPLN.getLastName()).isEqualTo("Sobalak");
        assertThat(accountDtoAfterExchangeToPLN.getUsername()).isEqualTo("Username");
        assertThat(accountDtoAfterExchangeToPLN.getBalances().stream().filter(balance -> Currency.PLN.equals(balance.getCurrency())).map(BalanceResponse::getAmount).findAny().get()).usingComparator(BigDecimal::compareTo).isGreaterThan(BigDecimal.valueOf(666.66).subtract(BigDecimal.valueOf(exchangeCurrencyPLNtoUSDRequest.getAmount())));
        assertThat(accountDtoAfterExchangeToPLN.getBalances().stream().filter(balance -> Currency.DOL.equals(balance.getCurrency())).map(BalanceResponse::getAmount).findAny().get()).usingComparator(BigDecimal::compareTo).isGreaterThan(BigDecimal.ZERO);

        //Fail to exchange more than balance
        ExchangeCurrencyRequest moreThanBalanceExchange = ExchangeCurrencyRequest.builder().fromCurrency(Currency.PLN).toCurrency(Currency.DOL).amount(666.66).build();
        String responseContent = mockMvc.perform(post("/v1/exchange?apiKey=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moreThanBalanceExchange))).andReturn().getResponse().getContentAsString();
        assertThat(responseContent).contains("it is insufficient to exchange");
    }
}