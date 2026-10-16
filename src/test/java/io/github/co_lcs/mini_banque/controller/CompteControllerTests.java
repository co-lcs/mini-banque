package io.github.co_lcs.mini_banque.controller;

import io.github.co_lcs.mini_banque.dto.CompteRequest;
import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.service.CompteNotFoundException;
import io.github.co_lcs.mini_banque.service.CompteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CompteController.class)
class CompteControllerTests {

    private static final String IBAN_VALIDE = "FR7630002000101234567890189";
    private static final String TITULAIRE_VALIDE = "titulaire test";
    private static final BigDecimal SOLDE_VALIDE = new BigDecimal("1000");
    private static final Currency EUR = Currency.getInstance("EUR");

    private static CompteRequest requete(String iban, String titulaire, BigDecimal solde, Currency devise) {
        return new CompteRequest(iban, titulaire, solde, devise);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompteService compteService;

    @Autowired
    private ObjectMapper objectMapper;

    // Cas nominaux

    @Test
    void shouldReturnCompteWhenIdExists() throws Exception {
        Compte mockCompte = createDefaultMockCompte();

        given(compteService.getCompteById(1L)).willReturn(mockCompte);

        mockMvc.perform(get("/api/comptes/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulaire").value("titulaire test"));
    }

    @Test
    void shouldReturn404WhenIdNotFound() throws Exception {
        given(compteService.getCompteById(99L)).willThrow(new CompteNotFoundException("Compte introuvable avec l'identifiant : 99"));

        mockMvc.perform(get("/api/comptes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Compte introuvable avec l'identifiant : 99"));
    }

    @Test
    void newCompteShouldReturn201AndLocationHeader() throws Exception {
        CompteRequest request = new CompteRequest(
                "FR7630002000101234567890189",
                "titulaire test",
                new BigDecimal("1000"),
                Currency.getInstance("EUR")
        );

        Compte savedCompte = createDefaultMockCompte();

        given(compteService.saveCompte(
                request.iban(),
                request.titulaire(),
                request.solde(),
                request.devise()
        )).willReturn(savedCompte);

        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/comptes/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulaire").value("titulaire test"))
                .andExpect(jsonPath("$.devise").value("EUR"));

    }

    @Test
    void shouldReturnListOfCompte() throws Exception {
        Compte mockCompte = createDefaultMockCompte();

        given(compteService.getComptes()).willReturn(List.of(mockCompte));

        mockMvc.perform(get("/api/comptes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulaire").value("titulaire test"));
    }

    // 409
    @Test
    void shouldReturn409WhenIbanAlreadyExists() throws Exception {
        CompteRequest request = new CompteRequest(IBAN_VALIDE, TITULAIRE_VALIDE, SOLDE_VALIDE, EUR);

        given(compteService.saveCompte(
                request.iban(),
                request.titulaire(),
                request.solde(),
                request.devise()
        )).willThrow(new DataIntegrityViolationException(
                "Unique index violation: UK_COMPTE_IBAN VALUES ('" + IBAN_VALIDE + "')"));

        String responseBody = mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Violation de contrainte"))
                .andReturn().getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain(IBAN_VALIDE).doesNotContain("UK_COMPTE_IBAN");
    }

    // 400 : Validation

    static Stream<Arguments> requetesAvecUnChampInvalide() {
        return Stream.of(
                arguments("iban", "minuscules", requete("fr7630002000101234567890189", TITULAIRE_VALIDE, SOLDE_VALIDE, EUR)),
                arguments("iban", "14 caractères", requete("FR763000200010", TITULAIRE_VALIDE, SOLDE_VALIDE, EUR)),
                arguments("iban", "35 caractères", requete("FR76" + "A".repeat(31), TITULAIRE_VALIDE, SOLDE_VALIDE, EUR)),
                arguments("iban", "blanc", requete("   ", TITULAIRE_VALIDE, SOLDE_VALIDE, EUR)),
                arguments("iban", "null", requete(null, TITULAIRE_VALIDE, SOLDE_VALIDE, EUR)),
                arguments("titulaire", "blanc", requete(IBAN_VALIDE, "   ", SOLDE_VALIDE, EUR)),
                arguments("titulaire", "1 caractère", requete(IBAN_VALIDE, "A", SOLDE_VALIDE, EUR)),
                arguments("titulaire", "101 caractères", requete(IBAN_VALIDE, "A".repeat(101), SOLDE_VALIDE, EUR)),
                arguments("titulaire", "null", requete(IBAN_VALIDE, null, SOLDE_VALIDE, EUR)),
                arguments("solde", "négatif", requete(IBAN_VALIDE, TITULAIRE_VALIDE, new BigDecimal("-0.01"), EUR)),
                arguments("solde", "3 décimales", requete(IBAN_VALIDE, TITULAIRE_VALIDE, new BigDecimal("10.123"), EUR)),
                arguments("solde", "13 chiffres entiers", requete(IBAN_VALIDE, TITULAIRE_VALIDE, new BigDecimal("1000000000000"), EUR)),
                arguments("solde", "null", requete(IBAN_VALIDE, TITULAIRE_VALIDE, null, EUR)),
                arguments("devise", "null", requete(IBAN_VALIDE, TITULAIRE_VALIDE, SOLDE_VALIDE, null))
        );
    }

    @ParameterizedTest(name = "{0} invalide : {1}")
    @MethodSource("requetesAvecUnChampInvalide")
    void shouldReturn400WhenOneFieldIsInvalid(String champ, String cas, CompteRequest request) throws Exception {
        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.errors", aMapWithSize(1)))
                .andExpect(jsonPath("$.errors." + champ).exists());

        then(compteService).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnAllIbanMessagesWhenIbanIsBlank() throws Exception {
        CompteRequest request = new CompteRequest(" ", TITULAIRE_VALIDE, SOLDE_VALIDE, EUR);

        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.iban", allOf(
                        containsString("L'iban doit être complet"),
                        containsString("L'iban doit faire entre 15 et 34 caractères"),
                        containsString("Format d'iban invalide"))));

        then(compteService).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnAllFieldErrorsWhenSeveralFieldsAreInvalid() throws Exception {
        CompteRequest request = new CompteRequest("invalide", "   ", new BigDecimal("-1"), null);

        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", aMapWithSize(4)))
                .andExpect(jsonPath("$.errors.iban").exists())
                .andExpect(jsonPath("$.errors.titulaire").exists())
                .andExpect(jsonPath("$.errors.solde").exists())
                .andExpect(jsonPath("$.errors.devise").exists());

        then(compteService).shouldHaveNoInteractions();
    }

    // 400 : corps illisible

    private static String corpsAvecDevise(String devise) {
        return """
                {"iban": "FR7630002000101234567890189", "titulaire": "titulaire test",
                 "solde": "40.00", "devise": "%s"}
                """.formatted(devise);
    }

    static Stream<Arguments> corpsIllisibles() {
        return Stream.of(
                arguments("solde non numérique", """
                        {"iban": "FR7630002000101234567890189", "titulaire": "titulaire test",
                         "solde": "abc", "devise": "EUR"}
                        """, "solde", "Valeur non supportée"),
                arguments("devise à 4 lettres", corpsAvecDevise("EURO"), "devise", "Valeur non supportée"),
                arguments("devise en minuscules", corpsAvecDevise("eur"), "devise", "Valeur non supportée"),
                arguments("devise inexistante", corpsAvecDevise("ZZZ"), "devise", "Valeur non supportée"),
                arguments("JSON tronqué", "{ \"iban\": ", "requête", "Format JSON invalide"),
                arguments("corps vide", "", "requête", "Format JSON invalide")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("corpsIllisibles")
    void shouldReturn400WhenBodyIsUnreadable(String cas, String body, String champ, String message) throws Exception {
        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("Format de requête invalide"))
                .andExpect(jsonPath("$.errors['" + champ + "']").value(message));

        then(compteService).shouldHaveNoInteractions();
    }

    // --- 400 : identifiant ---

    @ParameterizedTest(name = "id = {0}")
    @ValueSource(strings = {"abc", "1.5", "1a"})
    void shouldReturn400WhenIdIsNotNumeric(String id) throws Exception {
        mockMvc.perform(get("/api/comptes/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400));

        then(compteService).shouldHaveNoInteractions();
    }

    private Compte createDefaultMockCompte() {
        Compte mockCompte = mock(Compte.class);
        given(mockCompte.getId()).willReturn(1L);
        given(mockCompte.getIban()).willReturn("FR7630002000101234567890189");
        given(mockCompte.getTitulaire()).willReturn("titulaire test");
        given(mockCompte.getSolde()).willReturn(BigDecimal.valueOf(1000));
        given(mockCompte.getDateOuverture()).willReturn(LocalDate.of(2026, 1, 1));
        given(mockCompte.getDevise()).willReturn(Currency.getInstance("EUR"));
        return mockCompte;
    }
}
