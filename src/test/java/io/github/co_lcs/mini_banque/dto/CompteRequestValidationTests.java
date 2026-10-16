package io.github.co_lcs.mini_banque.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CompteRequestValidationTest {

    private static final String IBAN_VALIDE = "FR7630006000011234567890189";
    private static final String TITULAIRE_VALIDE = "Jean Dupont";
    private static final BigDecimal SOLDE_VALIDE = new BigDecimal("100.00");
    private static final Currency EUR = Currency.getInstance("EUR");

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    private static Set<String> champsEnErreur(CompteRequest request) {
        return validator.validate(request).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void requeteValide() {
        var request = new CompteRequest(IBAN_VALIDE, TITULAIRE_VALIDE, SOLDE_VALIDE, EUR);
        assertThat(champsEnErreur(request)).isEmpty();
    }

    // --- IBAN ---

    static Stream<String> ibansValides() {
        return Stream.of(
                "FR7630006000011",          // 15 caractères (borne basse)
                IBAN_VALIDE,                // 27 caractères
                "FR76" + "A".repeat(30)     // 34 caractères (borne haute)
        );
    }

    @ParameterizedTest(name = "iban valide : {0}")
    @MethodSource("ibansValides")
    void ibanValide(String iban) {
        var request = new CompteRequest(iban, TITULAIRE_VALIDE, SOLDE_VALIDE, EUR);
        assertThat(champsEnErreur(request)).isEmpty();
    }

    static Stream<String> ibansInvalides() {
        return Stream.of(
                null,
                "",
                "   ",
                "FR763000600001",                       // 14 caractères
                "FR76" + "A".repeat(31),                // 35 caractères
                "fr7630006000011234567890189",          // minuscules
                "1R7630006000011234567890189",          // pays non alphabétique
                "FRAB30006000011234567890189",          // clé non numérique
                "FR76 3000 6000 0112 3456 7890 189",    // espaces
                "FR76-30006000011234567890189"          // caractère spécial
        );
    }

    @ParameterizedTest(name = "iban invalide : [{0}]")
    @MethodSource("ibansInvalides")
    void ibanInvalide(String iban) {
        var request = new CompteRequest(iban, TITULAIRE_VALIDE, SOLDE_VALIDE, EUR);
        assertThat(champsEnErreur(request)).containsExactly("iban");
    }

    // --- Titulaire ---

    static Stream<String> titulairesValides() {
        return Stream.of(
                "Al",                   // 2 caractères (borne basse)
                TITULAIRE_VALIDE,
                "A".repeat(100)         // 100 caractères (borne haute)
        );
    }

    @ParameterizedTest(name = "titulaire valide : {0}")
    @MethodSource("titulairesValides")
    void titulaireValide(String titulaire) {
        var request = new CompteRequest(IBAN_VALIDE, titulaire, SOLDE_VALIDE, EUR);
        assertThat(champsEnErreur(request)).isEmpty();
    }

    static Stream<String> titulairesInvalides() {
        return Stream.of(
                null,
                "",
                "   ",
                "A",                    // 1 caractère
                "A".repeat(101)         // 101 caractères
        );
    }

    @ParameterizedTest(name = "titulaire invalide : [{0}]")
    @MethodSource("titulairesInvalides")
    void titulaireInvalide(String titulaire) {
        var request = new CompteRequest(IBAN_VALIDE, titulaire, SOLDE_VALIDE, EUR);
        assertThat(champsEnErreur(request)).containsExactly("titulaire");
    }

    // --- Solde ---

    static Stream<BigDecimal> soldesValides() {
        return Stream.of(
                new BigDecimal("0"),
                new BigDecimal("0.00"),
                new BigDecimal("0.01"),
                new BigDecimal("100.5"),
                new BigDecimal("999999999999.99")   // 12 chiffres entiers + 2 décimales
        );
    }

    @ParameterizedTest(name = "solde valide : {0}")
    @MethodSource("soldesValides")
    void soldeValide(BigDecimal solde) {
        var request = new CompteRequest(IBAN_VALIDE, TITULAIRE_VALIDE, solde, EUR);
        assertThat(champsEnErreur(request)).isEmpty();
    }

    static Stream<BigDecimal> soldesInvalides() {
        return Stream.of(
                null,
                new BigDecimal("-0.01"),
                new BigDecimal("-1"),
                new BigDecimal("0.001"),                // 3 décimales
                new BigDecimal("10.123"),               // 3 décimales
                new BigDecimal("1000000000000"),        // 13 chiffres entiers
                new BigDecimal("1000000000000.00")      // 13 chiffres entiers
        );
    }

    @ParameterizedTest(name = "solde invalide : {0}")
    @MethodSource("soldesInvalides")
    void soldeInvalide(BigDecimal solde) {
        var request = new CompteRequest(IBAN_VALIDE, TITULAIRE_VALIDE, solde, EUR);
        assertThat(champsEnErreur(request)).containsExactly("solde");
    }

    // --- Devise ---

    @Test
    void deviseNulleInvalide() {
        var request = new CompteRequest(IBAN_VALIDE, TITULAIRE_VALIDE, SOLDE_VALIDE, null);
        assertThat(champsEnErreur(request)).containsExactly("devise");
    }
}