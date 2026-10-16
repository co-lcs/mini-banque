package io.github.co_lcs.mini_banque.controller;

import io.github.co_lcs.mini_banque.dto.CompteDto;
import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.service.CompteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CompteController.class)
public class CompteControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompteService compteService;

    @Test
    void shouldReturnCompteWhenIdExists() throws Exception {
        Compte mockCompte = mock(Compte.class);
        given(mockCompte.getId()).willReturn(1L);
        given(mockCompte.getIban()).willReturn("FR7630002000101234567890189");
        given(mockCompte.getTitulaire()).willReturn("titulaire test");
        given(mockCompte.getSolde()).willReturn(BigDecimal.valueOf(1000));
        given(mockCompte.getDateOuverture()).willReturn(LocalDate.of(2026, 10, 12));
        given(mockCompte.getDevise()).willReturn(Currency.getInstance("EUR"));

        given(compteService.getCompteById(1L)).willReturn(mockCompte);

        mockMvc.perform(get("/api/comptes/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulaire").value("titulaire test"));
    }
}
