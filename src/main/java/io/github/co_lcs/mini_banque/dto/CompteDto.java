package io.github.co_lcs.mini_banque.dto;

import io.github.co_lcs.mini_banque.model.Compte;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompteDto (
    Long id,
    String iban,
    String titulaire,
    BigDecimal solde,
    LocalDate dateOuverture,
    String devise
) {
    public static CompteDto fromEntity(Compte compte) {
        return new CompteDto(
                compte.getId(),
                compte.getIban(),
                compte.getTitulaire(),
                compte.getSolde(),
                compte.getDateOuverture(),
                compte.getDevise().getDisplayName()
        );
    }
}
