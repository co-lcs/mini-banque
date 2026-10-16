package io.github.co_lcs.mini_banque.controller;

import io.github.co_lcs.mini_banque.dto.CompteRequest;
import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.service.CompteNotFoundException;
import io.github.co_lcs.mini_banque.service.CompteService;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompteService compteService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void shouldReturn409WhenIbanAlreadyExists() throws Exception {
        CompteRequest request = new CompteRequest(
                "FR7630002000101234567890189",
                "titulaire test",
                new BigDecimal("1000"),
                Currency.getInstance("EUR")
        );

        given(compteService.saveCompte(
                request.iban(),
                request.titulaire(),
                request.solde(),
                request.devise()
        )).willThrow(new DataIntegrityViolationException("Violation de contrainte"));

        mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Violation de contrainte"));
    }

    @Test
    void shouldReturn400WhenInvalidTitulaireProvided() throws Exception {
        String invalidTitulaire = "   ";
        CompteRequest request = new CompteRequest(
                "FR7630002000101234567890189",
                invalidTitulaire,
                new BigDecimal("1000"),
                Currency.getInstance("EUR")
        );
        String responseBody = mockMvc.perform(post("/api/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.errors.titulaire").exists())
                .andReturn().getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain(invalidTitulaire);

        then(compteService).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturn400WhenInvalidCurrencyProvided() throws Exception {
        String invalidJson = """
                {
                    "iban": "FR7630002000101234567890189",
                    "titulaire": "titulaire test",
                    "solde": "40.00",
                    "devise": "EURO"
                }
                """;
        mockMvc.perform(post("/api/comptes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.errors.devise").exists());

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
