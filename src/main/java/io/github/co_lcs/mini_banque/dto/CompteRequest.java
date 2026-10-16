package io.github.co_lcs.mini_banque.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Currency;

public record CompteRequest(
        @NotBlank(message = "L'iban doit être complet")
        @Size(min = 15, max = 34, message = "L'iban doit faire entre 15 et 34 caractères")
        @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$", message = "Format d'iban invalide")
        String iban,

        @NotBlank(message = "Le titulaire ne peut pas être vide")
        @Size(min = 2, max = 100, message = "Le titulaire doit faire entre 2 et 100 caractères")
        String titulaire,

        @NotNull(message = "Le solde ne peut pas être nul")
        @PositiveOrZero(message = "Le solde doit être supérieur ou égal à zéro")
        @Digits(integer = 12, fraction = 2, message = "Le solde doit comporter maximum 2 chiffres après la virgule et 12 avant")
        BigDecimal solde,

        @NotNull(message = "La devise ne peut pas être nulle")
        Currency devise
) {}
