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

    private record compteArguments(String iban, String titulaire, BigDecimal solde, LocalDate dateOuverture, Currency devise) {}

    private static Stream<compteArguments> invalidArgumentsProvider() {
        String validIban = "FR7630002000101234567890189";
        String validTitulaire = "Titulaire Test";
        BigDecimal validSolde = new BigDecimal("40.00");
        LocalDate validDateOuverture = LocalDate.of(2026, 10, 12);
        Currency validDevise = Currency.getInstance("EUR");

        return Stream.of(
                new compteArguments(null, validTitulaire, validSolde, validDateOuverture, validDevise),
                new compteArguments(validIban, null, validSolde, validDateOuverture, validDevise),
                new compteArguments(validIban, validTitulaire, null, validDateOuverture, validDevise),
                new compteArguments(validIban, validTitulaire, validSolde, null, validDevise),
                new compteArguments(validIban, validTitulaire, validSolde, validDateOuverture, null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidArgumentsProvider")
    @DisplayName("Doit rejeter l'instancisation si un champts est nul")
    void shouldRejectInstanciationWhenFieldIsNull(compteArguments args) {
        assertThatThrownBy(() -> new Compte(args.iban(), args.titulaire(), args.solde(), args.dateOuverture(), args.devise())).isInstanceOf(NullPointerException.class);
    }

}

