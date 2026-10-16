package io.github.co_lcs.mini_banque.dto;

import io.github.co_lcs.mini_banque.model.Compte;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompteResponse(
    Long id,
    String iban,
    String titulaire,
    BigDecimal solde,
    LocalDate dateOuverture,
    String devise
) {
    public static CompteResponse fromEntity(Compte compte) {
        return new CompteResponse(
                compte.getId(),
                compte.getIban(),
                compte.getTitulaire(),
                compte.getSolde(),
                compte.getDateOuverture(),
                compte.getDevise().getCurrencyCode()
        );
    }
}
