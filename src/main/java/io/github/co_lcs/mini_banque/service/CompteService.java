package io.github.co_lcs.mini_banque.service;

import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.repository.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Currency;

@Service
public class CompteService {

    private final CompteRepository compteRepository;
    private final Clock clock;

    public CompteService(CompteRepository compteRepository, Clock clock) {
        this.compteRepository = compteRepository;
        this.clock = clock;
    }

    public Compte saveCompte(String iban, String titulaire, BigDecimal solde, Currency devise){
        LocalDate dateOuverture = LocalDate.now(clock);
        Compte compte = new Compte(iban, titulaire, solde, dateOuverture, devise);
        return compteRepository.save(compte);
    }

    public Compte getCompteById(final Long id){
        return compteRepository.findById(id).orElseThrow(() -> new CompteNotFoundException("Compte introuvable avec l'identifiant : " + id));
    }

    public Iterable<Compte> getComptes() {
        return compteRepository.findAll();
    }

}
