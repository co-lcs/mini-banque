package io.github.co_lcs.mini_banque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompteDto (
    Long id,
    String iban,
    String titulaire,
    BigDecimal solde,
    LocalDate dateOuverture,
    String devise
) {}
