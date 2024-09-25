package com.cinkciarz.dao;

import com.cinkciarz.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByApiKey(String apiKey);
}