package com.cinkciarz.service;

import com.cinkciarz.dao.AccountRepository;
import com.cinkciarz.dto.AccountCreationRequest;
import com.cinkciarz.dto.AccountDto;
import com.cinkciarz.exception.CinkciarzAccountInvalidDataException;
import com.cinkciarz.exception.CinkciarzAccountNotFoundException;
import com.cinkciarz.mapper.AccountMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public String createNewAccount(AccountCreationRequest accountCreationRequest) {
        try {
            return accountRepository.save(accountMapper.mapToAccount(accountCreationRequest)).getApiKey();
        } catch (Exception ex) {
            throw new CinkciarzAccountInvalidDataException("Username must be unique");
        }
    }

    public AccountDto getAccountByApiKey(String apiKey) {
        return accountRepository.findByApiKey(apiKey).map(accountMapper::mapToAccountResponse).orElseThrow(() -> new CinkciarzAccountNotFoundException(apiKey));
    }
}
