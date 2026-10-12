package io.github.co_lcs.mini_banque.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CompteTests {

    private record CompteArguments(String iban, String titulaire, BigDecimal solde, LocalDate dateOuverture, Currency devise) {}

    private static Stream<CompteArguments> invalidArgumentsProvider() {
        String validIban = "FR7630002000101234567890189";
        String validTitulaire = "Titulaire Test";
        BigDecimal validSolde = new BigDecimal("40.00");
        LocalDate validDateOuverture = LocalDate.of(2026, 10, 12);
        Currency validDevise = Currency.getInstance("EUR");

        return Stream.of(
                new CompteArguments(null, validTitulaire, validSolde, validDateOuverture, validDevise),
                new CompteArguments(validIban, null, validSolde, validDateOuverture, validDevise),
                new CompteArguments(validIban, validTitulaire, null, validDateOuverture, validDevise),
                new CompteArguments(validIban, validTitulaire, validSolde, null, validDevise),
                new CompteArguments(validIban, validTitulaire, validSolde, validDateOuverture, null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidArgumentsProvider")
    @DisplayName("Doit rejeter l'instantiation si un champs est nul")
    void shouldRejectInstanciationWhenFieldIsNull(CompteArguments args) {
        assertThatThrownBy(() -> new Compte(args.iban(), args.titulaire(), args.solde(), args.dateOuverture(), args.devise())).isInstanceOf(NullPointerException.class);
    }

}

