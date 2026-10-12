package io.github.co_lcs.mini_banque.service;

import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.repository.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class CompteService {

    @Autowired
    private CompteRepository compteRepository;
    private final Clock clock;

    public CompteService(CompteRepository compteRepository, Clock clock) {
        this.compteRepository = compteRepository;
        this.clock = clock;
    }

    public Compte saveCompte(Compte compte){
        return compteRepository.save(compte);
    }

    public Compte getCompteById(final Long id){
        return compteRepository.findById(id).orElseThrow(() -> new CompteNotFoundException("Compte introuvable : " + id));

    }

    public Iterable<Compte> getComptes() {
        return compteRepository.findAll();
    }

}
