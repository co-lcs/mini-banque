package io.github.co_lcs.mini_banque.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CompteTests {
    private static Stream<Arguments> invalidArgumentsProvider() {
        String validIban = "FR7630002000101234567890189";
        String validTitulaire = "Titulaire Test";
        BigDecimal validSolde = new BigDecimal("40.00");
        LocalDate validDateOuverture = LocalDate.of(2026, 10, 12);
        Currency validDevise = Currency.getInstance("EUR");

        return Stream.of(
                Arguments.of(null, validTitulaire, validSolde, validDateOuverture, validDevise, "iban obligatoire"),
                Arguments.of(validIban, null, validSolde, validDateOuverture, validDevise, "titulaire obligatoire"),
                Arguments.of(validIban, validTitulaire, null, validDateOuverture, validDevise, "solde obligatoire"),
                Arguments.of(validIban, validTitulaire, validSolde, null, validDevise, "dateOuverture obligatoire"),
                Arguments.of(validIban, validTitulaire, validSolde, validDateOuverture, null, "devise obligatoire")
        );
    }

    @ParameterizedTest(name = "[{index}] Doit rejeter l'instanciation si {5}")
    @MethodSource("invalidArgumentsProvider")
    void shouldRejectInstantiationWhenFieldIsNull(
        String iban,
        String titulaire,
        BigDecimal solde,
        LocalDate dateOuverture,
        Currency devise,
        String expectedMessage) {
        assertThatThrownBy(() -> new Compte(iban, titulaire, solde, dateOuverture, devise)).isInstanceOf(NullPointerException.class).hasMessage(expectedMessage);
    }
}