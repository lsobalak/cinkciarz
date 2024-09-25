package com.cinkciarz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRatesSeries {

    private String table;
    private String currency;
    private String code;

    @JsonProperty("rates")
    private List<Rate> rates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rate {

        private String no;

        @JsonProperty("effectiveDate")
        private String effectiveDate;

        private BigDecimal mid;
    }
}
