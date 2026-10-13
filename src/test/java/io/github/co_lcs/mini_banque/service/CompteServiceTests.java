package io.github.co_lcs.mini_banque.service;

import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.repository.CompteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompteServiceTests {
    private CompteService compteService;
    private Clock fixedClock;

    @Mock
    private CompteRepository compteRepository;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-06-15T23:30:00Z"), ZoneId.of("Europe/Paris"));
        compteService = new CompteService(compteRepository, fixedClock);
    }

    @Test
    void saveCompteShouldSetDateFromClockAndPersist() {
        compteService.saveCompte("FR7630002000101234567890189", "titulaire test", BigDecimal.valueOf(1000), Currency.getInstance("EUR"));

        ArgumentCaptor<Compte> captor = ArgumentCaptor.forClass(Compte.class);
        verify(compteRepository).save(captor.capture());

        assertThat(captor.getValue().getDateOuverture()).isEqualTo(LocalDate.of(2026, 6, 16));
    }

    @Test
    void getCompteByIdShouldThrowExceptionWhenIdNotFound(){
        when(compteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> compteService.getCompteById(99L)).isInstanceOf(CompteNotFoundException.class).hasMessage("Compte introuvable avec l'identifiant : 99");
    }

    @Test
    void getCompteByIdShouldReturnCompteWhenIdExists() {
        Compte testCompte = new Compte("FR7630002000101234567890189", "Titulaire Test", new BigDecimal("40.00"), LocalDate.of(2026, 10, 12), Currency.getInstance("EUR"));
        when(compteRepository.findById(1L)).thenReturn(Optional.of(testCompte));

        Compte result = compteService.getCompteById(1L);

        assertThat(result).isSameAs(testCompte);
        assertThat(result.getIban()).isEqualTo("FR7630002000101234567890189");
    }
}
