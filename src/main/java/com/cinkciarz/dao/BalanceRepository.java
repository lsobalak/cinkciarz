package com.cinkciarz.dao;

import com.cinkciarz.model.Balance;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface BalanceRepository extends CrudRepository<Balance, Long> {

    Set<Balance> findByAccountApiKey(String apiKey);
}