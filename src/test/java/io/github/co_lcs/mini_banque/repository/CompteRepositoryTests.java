package io.github.co_lcs.mini_banque.repository;

import io.github.co_lcs.mini_banque.model.Compte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@DataJpaTest
public class CompteRepositoryTests {
    @Autowired
    private CompteRepository compteRepository;

    @Test
    @DisplayName("Doit persister le compte et générer un identifiant lorsque les données sont valides")
    void shouldPersistCompteWhenValidDataProvided() {
        Compte testCompte = new Compte("FR7630002000101234567890189", "Titulaire Test", new BigDecimal("40.00"), LocalDate.of(2026, 10, 12), Currency.getInstance("EUR"));
        Compte savedCompte = compteRepository.save(testCompte);
        assertThat(savedCompte.getId()).isNotNull().isPositive();
    }

}
