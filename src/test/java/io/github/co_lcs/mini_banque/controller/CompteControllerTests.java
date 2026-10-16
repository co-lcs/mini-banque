package io.github.co_lcs.mini_banque.controller;

import io.github.co_lcs.mini_banque.dto.CreateCompteRequest;
import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.service.CompteNotFoundException;
import io.github.co_lcs.mini_banque.service.CompteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        CreateCompteRequest request = new CreateCompteRequest(
                "FR7630002000101234567890189",
                "Titulaire Test",
                new BigDecimal("40.00"),
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
