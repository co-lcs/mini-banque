package io.github.co_lcs.mini_banque.dto;

import java.math.BigDecimal;
import java.util.Currency;

public record CreateCompteRequest(
        String iban,
        String titulaire,
        BigDecimal solde,
        Currency devise
) {}
